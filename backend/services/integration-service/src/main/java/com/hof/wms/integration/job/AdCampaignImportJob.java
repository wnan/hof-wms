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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 广告活动数据导入Quartz Job
 * 导入最近两天的数据，并删除昨天的数据
 */
@Slf4j
@Component
public class AdCampaignImportJob implements Job {

    @Autowired
    private SellFoxApiClient apiClient;

    @Autowired
    private SfImportService sfImportService;

    @Autowired
    private SyncLogRepository syncLogRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DOWNLOAD_DIR = "./data/downloads";
    private static final long POLL_INTERVAL_MS = 60_000;
    private static final long MAX_POLL_TIME_MS = 30 * 60_000;

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Long taskConfigId = dataMap.getLong("taskConfigId");
        String taskName = dataMap.getString("taskName");
        String paramsJson = dataMap.getString("params");

        log.info("===== 开始执行广告活动数据导入任务: {} =====", taskName);

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
            AdCampaignImportParams params = sfImportService.resolveParams(paramsJson, AdCampaignImportParams.class);
            String adTypeCode = params.getAdTypeCode();
            List<String> reportTypeCodes = params.getEffectiveReportTypeCodes();
            String timeUnit = params.getTimeUnit();
            String startDate = params.getStartDateExpr() != null ? params.getStartDateExpr()
                    : LocalDate.now().minusDays(1).format(DATE_FMT);
            String endDate = params.getEndDateExpr() != null ? params.getEndDateExpr()
                    : LocalDate.now().format(DATE_FMT);

            LocalDate reportStartDate = LocalDate.parse(startDate);
            LocalDate reportEndDate = LocalDate.parse(endDate);

            log.info("导入参数 - 广告类型: {}, 报告类型: {}, 日期范围: {} ~ {}",
                    adTypeCode, reportTypeCodes, reportStartDate, reportEndDate);

            ApiResponse<?> tokenResponse = apiClient.getAccessToken();
            if (tokenResponse == null || !tokenResponse.isSuccess()) {
                throw new IOException("获取Access Token失败");
            }

            List<ShopInfo> shops = sfImportService.getAllShops();
            if (shops.isEmpty()) {
                log.warn("未获取到店铺数据，跳过广告活动导入");
                syncLog.setEndTime(LocalDateTime.now());
                syncLog.setStatus("success");
                syncLogRepository.save(syncLog);
                sfImportService.updateTaskExecuteStatus(taskConfigId, "SUCCESS", "无店铺数据");
                return;
            }

            int totalImported = 0;
            int failCount = 0;
            StringBuilder messageBuilder = new StringBuilder();

            for (String reportTypeCode : reportTypeCodes) {
                log.info("开始处理报告类型: {} (广告类型: {})", reportTypeCode, adTypeCode);

                try {
                    int imported = processShops(shops, adTypeCode, reportTypeCode, timeUnit,
                            reportStartDate, reportEndDate);
                    totalImported += imported;
                    messageBuilder.append(reportTypeCode)
                            .append(": 导入").append(imported).append("条; ");
                } catch (Exception e) {
                    failCount++;
                    log.error("处理报告类型 {} 失败: {}", reportTypeCode, e.getMessage());
                    messageBuilder.append(reportTypeCode)
                            .append(": 失败 - ").append(e.getMessage()).append("; ");
                }
            }

            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setSyncCount(totalImported);
            syncLog.setSuccessCount(totalImported);
            syncLog.setFailCount(failCount);
            syncLog.setStatus(failCount > 0 ? "partial" : "success");
            syncLogRepository.save(syncLog);

            sfImportService.updateTaskExecuteStatus(taskConfigId, "SUCCESS",
                    messageBuilder.toString());
            log.info("===== 广告活动数据导入任务完成: {}，共导入 {} 条 =====", taskName, totalImported);

        } catch (Exception e) {
            log.error("广告活动数据导入任务失败: {}", e.getMessage(), e);
            syncLog.setEndTime(LocalDateTime.now());
            syncLog.setStatus("failed");
            syncLog.setErrorMessage(e.getMessage());
            syncLogRepository.save(syncLog);
            sfImportService.updateTaskExecuteStatus(taskConfigId, "FAILED", e.getMessage());
        }
    }

    private int processShops(List<ShopInfo> shops, String adTypeCode, String reportTypeCode,
                              String timeUnit, LocalDate startDate, LocalDate endDate) throws Exception {
        List<String> shopIds = shops.stream().map(ShopInfo::getId).collect(Collectors.toList());
        Map<String, String> shopNameToIdMap = shops.stream()
                .filter(s -> s.getName() != null)
                .collect(Collectors.toMap(ShopInfo::getName, ShopInfo::getId, (a, b) -> a));

        log.info("一次性处理 {} 个店铺，shopIds: {}", shopIds.size(), shopIds);

        CreateTaskRequest request = new CreateTaskRequest();
        request.setShopIds(shopIds);
        request.setAdTypeCode(adTypeCode);
        request.setReportTypeCode(reportTypeCode);
        request.setTimeUnit(timeUnit);
        request.setReportStartDate(startDate.format(DATE_FMT));
        request.setReportEndDate(endDate.format(DATE_FMT));

        ApiResponse<CreateTaskResponse> response = apiClient.createTask(request);
        if (response == null || !response.isSuccess()) {
            throw new IOException("创建报表任务失败: " + (response != null ? response.getMsg() : "未知错误"));
        }

        String taskId = response.getData().getId();
        log.info("报表任务已创建，TaskID: {}, 包含 {} 个店铺", taskId, shopIds.size());

        TaskInfo taskInfo = pollTaskUntilComplete(taskId);
        if (taskInfo == null) {
            throw new IOException("任务未完成或超时: " + taskId);
        }

        List<String> downloadUrls = taskInfo.getDownloadUrl();
        if (downloadUrls == null || downloadUrls.isEmpty()) {
            log.warn("报表任务没有下载链接");
            return 0;
        }

        String taskDir = DOWNLOAD_DIR + "/" + taskId;
        Files.createDirectories(Paths.get(taskDir));

        int totalImported = 0;
        for (int i = 0; i < downloadUrls.size(); i++) {
            String url = downloadUrls.get(i);
            String fileName = taskInfo.getTaskName() + "_" + (i + 1) + getFileExtension(url);
            String savePath = taskDir + "/" + fileName;

            apiClient.downloadFile(url, savePath);
            log.info("报表文件已下载: {}", savePath);

            int imported = sfImportService.importAdCampaignReport(
                    savePath, shopNameToIdMap, reportTypeCode, adTypeCode);
            totalImported += imported;
        }

        return totalImported;
    }

    private TaskInfo pollTaskUntilComplete(String taskId) throws Exception {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < MAX_POLL_TIME_MS) {
            TaskQueryRequest queryRequest = new TaskQueryRequest();
            queryRequest.setTaskIds(List.of(taskId));
            queryRequest.setPageNo(1);
            queryRequest.setPageSize(1);

            ApiResponse<TaskListResponse> response = apiClient.queryTaskList(queryRequest);
            if (response != null && response.isSuccess()) {
                List<TaskInfo> tasks = response.getData().getRows();
                if (!tasks.isEmpty()) {
                    TaskInfo task = tasks.get(0);
                    String state = task.getReportState();
                    log.debug("任务 {} 状态: {}", taskId, state);

                    if ("COMPLETED".equals(state) || "已生成".equals(state)) {
                        log.info("任务 {} 已完成", taskId);
                        return task;
                    } else if ("FAILED".equals(state) || "ERROR".equals(state)) {
                        log.error("任务 {} 失败", taskId);
                        return null;
                    }
                }
            }

            Thread.sleep(POLL_INTERVAL_MS);
        }

        log.error("任务 {} 轮询超时", taskId);
        return null;
    }

    private String getFileExtension(String url) {
        if (url.contains(".xlsx")) return ".xlsx";
        if (url.contains(".xls")) return ".xls";
        if (url.contains(".csv")) return ".csv";
        if (url.contains(".zip")) return ".zip";
        return ".xlsx";
    }
}
