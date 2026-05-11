package com.hof.wms.integration.job;

import com.hof.wms.integration.client.SellFoxApiClient;
import com.hof.wms.integration.entity.SyncLog;
import com.hof.wms.integration.entity.SyncTask;
import com.hof.wms.integration.model.*;
import com.hof.wms.integration.repository.SyncLogRepository;
import com.hof.wms.integration.service.SfImportService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 广告组合(Portfolio)导入Quartz Job
 * 按店铺遍历，使用游标分页获取Portfolio数据，全覆盖导入
 */
@Slf4j
@Component
public class PortfolioImportJob implements Job {

    @Autowired
    private SellFoxApiClient apiClient;

    @Autowired
    private SfImportService sfImportService;

    @Autowired
    private SyncLogRepository syncLogRepository;

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Long taskConfigId = dataMap.getLong("taskConfigId");
        String taskName = dataMap.getString("taskName");
        String paramsJson = dataMap.getString("params");

        log.info("===== 开始执行广告组合导入任务: {} =====", taskName);

        SyncLog syncLog = new SyncLog();
        syncLog.setTaskId(taskConfigId);
        syncLog.setTaskName(taskName);
        syncLog.setStartTime(LocalDateTime.now());
        syncLog.setSyncCount(0);
        syncLog.setSuccessCount(0);
        syncLog.setFailCount(0);
        syncLog.setStatus("running");
        syncLogRepository.save(syncLog);

        try {
            PortfolioImportParams params = sfImportService.resolveParams(paramsJson, PortfolioImportParams.class);
            String pageSize = params.getPageSize();

            ApiResponse<?> tokenResponse = apiClient.getAccessToken();
            if (tokenResponse == null || !tokenResponse.isSuccess()) {
                throw new IOException("获取Access Token失败");
            }

            // 从shop_info表获取所有店铺
            List<ShopInfo> shops = sfImportService.getAllShops();
            if (shops.isEmpty()) {
                log.warn("未获取到店铺数据，跳过导入");
                syncLog.setEndTime(LocalDateTime.now());
                syncLog.setStatus("success");
                syncLogRepository.save(syncLog);
                sfImportService.updateTaskExecuteStatus(taskConfigId, "SUCCESS", "未获取到店铺数据");
                return;
            }

            log.info("从shop_info获取到 {} 个店铺，开始逐店获取Portfolio数据", shops.size());

            // 逐店获取Portfolio数据
            List<PortfolioInfo> allPortfolios = new ArrayList<>();
            for (ShopInfo shop : shops) {
                log.info("正在获取店铺 {} ({}) 的Portfolio数据", shop.getName(), shop.getId());
                try {
                    List<PortfolioInfo> shopPortfolios = fetchAllPortfolios(shop.getId(), pageSize);
                    // 设置shopId（API返回的shopId可能为空，使用本地shop_info的id）
                    for (PortfolioInfo p : shopPortfolios) {
                        if (p.getShopId() == null || p.getShopId().isEmpty()) {
                            p.setShopId(shop.getId());
                        }
                    }
                    allPortfolios.addAll(shopPortfolios);
                    log.info("店铺 {} 获取到 {} 条Portfolio数据", shop.getName(), shopPortfolios.size());
                } catch (Exception e) {
                    log.error("获取店铺 {} 的Portfolio数据失败: {}", shop.getName(), e.getMessage(), e);
                }
            }

            log.info("从API获取到 {} 条Portfolio数据", allPortfolios.size());

            if (allPortfolios.isEmpty()) {
                log.warn("未获取到Portfolio数据，跳过导入");
                syncLog.setEndTime(LocalDateTime.now());
                syncLog.setStatus("success");
                syncLogRepository.save(syncLog);
                sfImportService.updateTaskExecuteStatus(taskConfigId, "SUCCESS", "未获取到Portfolio数据");
                return;
            }

            int imported = sfImportService.fullImportPortfolioInfo(allPortfolios);

            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setSyncCount(imported);
            syncLog.setSuccessCount(imported);
            syncLog.setFailCount(0);
            syncLog.setStatus("success");
            syncLogRepository.save(syncLog);

            sfImportService.updateTaskExecuteStatus(taskConfigId, "SUCCESS",
                    "导入完成，共 " + imported + " 条Portfolio数据");
            log.info("===== 广告组合导入任务完成: {}，共导入 {} 条 =====", taskName, imported);

        } catch (Exception e) {
            log.error("广告组合导入任务失败: {}", e.getMessage(), e);
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setStatus("failed");
            syncLog.setErrorMessage(e.getMessage());
            syncLogRepository.save(syncLog);
            sfImportService.updateTaskExecuteStatus(taskConfigId, "FAILED", e.getMessage());
        }
    }

    /**
     * 使用游标分页获取指定店铺的所有Portfolio数据
     */
    private List<PortfolioInfo> fetchAllPortfolios(String shopId, String pageSize) throws IOException {
        List<PortfolioInfo> allPortfolios = new ArrayList<>();
        String nextToken = null;

        do {
            ApiResponse<PortfolioListResponse> response = apiClient.getPortfolioList(shopId, nextToken, pageSize);
            if (response == null || !response.isSuccess()) {
                throw new IOException("获取Portfolio列表失败(shopId=" + shopId + "): "
                        + (response != null ? response.getMsg() : "未知错误"));
            }

            PortfolioListResponse data = response.getData();
            if (data != null && data.getItemList() != null) {
                allPortfolios.addAll(data.getItemList());
                nextToken = data.getNextToken();
            } else {
                nextToken = null;
            }
        } while (nextToken != null && !nextToken.isEmpty());

        return allPortfolios;
    }
}
