package com.hof.wms.integration.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hof.wms.integration.entity.AdCampaignReport;
import com.hof.wms.integration.entity.SyncTask;
import com.hof.wms.integration.mapper.AdCampaignReportMapper;
import com.hof.wms.integration.mapper.ShopInfoMapper;
import com.hof.wms.integration.mapper.SyncTaskMapper;
import com.hof.wms.integration.model.ShopInfo;
import com.hof.wms.integration.util.SpelExpressionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * SellFox数据导入服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SfImportService {

    private final ShopInfoMapper shopInfoMapper;
    private final AdCampaignReportMapper adCampaignReportMapper;
    private final AdCampaignParseService adCampaignParseService;
    private final SyncTaskMapper syncTaskMapper;
    private static final Gson GSON = new Gson();

    // ======================== ShopInfo 导入（全覆盖） ========================

    @Transactional
    public int fullImportShopInfo(List<ShopInfo> shops) {
        log.info("开始全覆盖导入店铺信息，共 {} 条", shops.size());

        shopInfoMapper.delete(null);
        log.info("已删除旧数据");

        int inserted = 0;
        for (ShopInfo shop : shops) {
            shopInfoMapper.insert(shop);
            inserted++;
        }

        log.info("店铺信息导入完成，插入 {} 条", inserted);
        return inserted;
    }

    // ======================== 广告活动数据导入 ========================

    @Transactional
    public int importAdCampaignReport(String filePath, String shopId, LocalDate deleteDate,
                                      LocalDate reportStartDate, LocalDate reportEndDate) throws IOException {
        log.info("开始导入广告活动数据，文件: {}, 删除日期: {}, 报告日期范围: {} ~ {}",
                filePath, deleteDate, reportStartDate, reportEndDate);

        List<AdCampaignReport> reports = adCampaignParseService.parseExcel(filePath, shopId);
        if (reports.isEmpty()) {
            log.warn("Excel解析结果为空，跳过导入");
            return 0;
        }

        if (deleteDate != null) {
            int deleted = adCampaignReportMapper.delete(
                    new LambdaQueryWrapper<AdCampaignReport>()
                            .eq(AdCampaignReport::getReportDate, deleteDate));
            log.info("已删除 {} 的 {} 条旧数据", deleteDate, deleted);
        }

        int deletedRange = adCampaignReportMapper.delete(
                new LambdaQueryWrapper<AdCampaignReport>()
                        .between(AdCampaignReport::getReportDate, reportStartDate, reportEndDate));
        log.info("已删除日期范围 {} ~ {} 的 {} 条旧数据", reportStartDate, reportEndDate, deletedRange);

        int inserted = 0;
        for (AdCampaignReport report : reports) {
            inserted += adCampaignReportMapper.upsert(report);
        }

        log.info("广告活动数据导入完成，插入 {} 条", inserted);
        return inserted;
    }

    // ======================== 任务配置管理 ========================

    public List<SyncTask> getAllTaskConfigs() {
        return syncTaskMapper.selectList(null);
    }

    public SyncTask getTaskConfigById(Long id) {
        return syncTaskMapper.selectById(id);
    }

    public Map<String, String> resolveParams(String paramsJson) {
        if (paramsJson == null || paramsJson.isEmpty()) {
            return Map.of();
        }

        Map<String, String> params = GSON.fromJson(paramsJson, new TypeToken<Map<String, String>>() {}.getType());
        Map<String, String> resolved = new java.util.HashMap<>();

        params.forEach((key, value) -> {
            if (SpelExpressionUtil.isExpression(value)) {
                String evaluated = SpelExpressionUtil.evaluateAsString(value);
                log.info("SpEL表达式求值: {} = {} -> {}", key, value, evaluated);
                resolved.put(key, evaluated);
            } else {
                resolved.put(key, value);
            }
        });

        return resolved;
    }

    /**
     * 将params JSON反序列化为具体类对象，并对其中的SpEL表达式字段求值
     */
    @SuppressWarnings("unchecked")
    public <T> T resolveParams(String paramsJson, Class<T> paramsClass) {
        if (paramsJson == null || paramsJson.isEmpty()) {
            try {
                return paramsClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("无法创建参数类实例: " + paramsClass.getName(), e);
            }
        }

        T params = GSON.fromJson(paramsJson, paramsClass);
        resolveSpelFields(params);
        return params;
    }

    /**
     * 根据paramsClass全限定名反序列化参数
     */
    @SuppressWarnings("unchecked")
    public <T> T resolveParams(String paramsJson, String paramsClassName) {
        if (paramsClassName == null || paramsClassName.isEmpty()) {
            throw new RuntimeException("paramsClass不能为空");
        }
        try {
            Class<T> clazz = (Class<T>) Class.forName(paramsClassName);
            return resolveParams(paramsJson, clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("未找到参数类: " + paramsClassName, e);
        }
    }

    /**
     * 递归解析对象中所有String字段的SpEL表达式
     */
    private void resolveSpelFields(Object obj) {
        if (obj == null) return;
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    String value = (String) field.get(obj);
                    if (value != null && SpelExpressionUtil.isExpression(value)) {
                        String evaluated = SpelExpressionUtil.evaluateAsString(value);
                        log.info("SpEL表达式求值: {}.{} = {} -> {}", obj.getClass().getSimpleName(), field.getName(), value, evaluated);
                        field.set(obj, evaluated);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析SpEL表达式字段失败", e);
        }
    }

    public void updateTaskExecuteStatus(Long taskId, String status, String message) {
        SyncTask task = syncTaskMapper.selectById(taskId);
        if (task != null) {
            task.setLastSyncTime(LocalDateTime.now());
            task.setLastExecuteStatus(status);
            task.setLastExecuteMessage(message);
            task.setUpdatedAt(LocalDateTime.now());
            syncTaskMapper.updateById(task);
        }
    }

    public void updateTaskConfig(SyncTask task) {
        task.setUpdatedAt(LocalDateTime.now());
        syncTaskMapper.updateById(task);
    }
}
