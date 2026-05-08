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
 * 店铺信息导入Quartz Job
 * 全覆盖导入：删除所有旧数据，插入API获取的所有店铺
 */
@Slf4j
@Component
public class ShopInfoImportJob implements Job {

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

        log.info("===== 开始执行店铺信息导入任务: {} =====", taskName);

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
            ShopInfoImportParams params = sfImportService.resolveParams(paramsJson, ShopInfoImportParams.class);
            String pageSize = params.getPageSize();

            ApiResponse<?> tokenResponse = apiClient.getAccessToken();
            if (tokenResponse == null || !tokenResponse.isSuccess()) {
                throw new IOException("获取Access Token失败");
            }

            List<ShopInfo> allShops = fetchAllShops(pageSize);
            log.info("从API获取到 {} 个店铺", allShops.size());

            if (allShops.isEmpty()) {
                log.warn("未获取到店铺数据，跳过导入");
                syncLog.setEndTime(LocalDateTime.now());
                syncLog.setStatus("success");
                syncLogRepository.save(syncLog);
                sfImportService.updateTaskExecuteStatus(taskConfigId, "SUCCESS", "未获取到店铺数据");
                return;
            }

            int imported = sfImportService.fullImportShopInfo(allShops);

            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setSyncCount(imported);
            syncLog.setSuccessCount(imported);
            syncLog.setFailCount(0);
            syncLog.setStatus("success");
            syncLogRepository.save(syncLog);

            sfImportService.updateTaskExecuteStatus(taskConfigId, "SUCCESS",
                    "导入完成，共 " + imported + " 条店铺数据");
            log.info("===== 店铺信息导入任务完成: {}，共导入 {} 条 =====", taskName, imported);

        } catch (Exception e) {
            log.error("店铺信息导入任务失败: {}", e.getMessage(), e);
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setStatus("failed");
            syncLog.setErrorMessage(e.getMessage());
            syncLogRepository.save(syncLog);
            sfImportService.updateTaskExecuteStatus(taskConfigId, "FAILED", e.getMessage());
        }
    }

    private List<ShopInfo> fetchAllShops(String pageSize) throws IOException {
        List<ShopInfo> allShops = new ArrayList<>();
        int pageNo = 1;
        int totalPages = 1;

        while (pageNo <= totalPages) {
            ApiResponse<ShopListResponse> response = apiClient.getShopList(
                    String.valueOf(pageNo), pageSize);
            if (response == null || !response.isSuccess()) {
                throw new IOException("获取店铺列表失败: " + (response != null ? response.getMsg() : "未知错误"));
            }

            ShopListResponse data = response.getData();
            if (data != null && data.getRows() != null) {
                allShops.addAll(data.getRows());
                totalPages = data.getTotalPage();
            }

            pageNo++;
        }

        return allShops;
    }
}
