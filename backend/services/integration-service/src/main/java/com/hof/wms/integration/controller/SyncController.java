package com.hof.wms.integration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.common.result.ApiResult;
import com.hof.wms.common.result.PageResult;
import com.hof.wms.integration.entity.SyncLog;
import com.hof.wms.integration.entity.SyncTask;
import com.hof.wms.integration.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/data-sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @GetMapping("/list")
    public ApiResult<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<SyncTask> page = syncService.getTaskPage(pageNum, pageSize, keyword);
        List<Map<String, Object>> rows = page.getRecords().stream()
                .map(this::toTaskMap)
                .collect(Collectors.toList());
        return ApiResult.success(new PageResult<>(rows, page.getTotal(), (long) pageNum, (long) pageSize));
    }

    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> detail(@PathVariable Long id) {
        SyncTask task = syncService.getTaskById(id);
        if (task == null) {
            return ApiResult.fail("同步任务不存在");
        }
        return ApiResult.success(toTaskMap(task));
    }

    @PostMapping("/save")
    public ApiResult<Map<String, Object>> save(@RequestBody Map<String, Object> command) {
        SyncTask task = toTask(command);
        SyncTask saved;
        if (task.getId() != null) {
            saved = syncService.updateTask(task.getId(), task);
            return ApiResult.success("更新成功", toTaskMap(saved));
        } else {
            saved = syncService.createTask(task);
            return ApiResult.success("保存成功", toTaskMap(saved));
        }
    }

    @PostMapping("/{id}/test")
    public ApiResult<Map<String, Object>> test(@PathVariable Long id) {
        Map<String, Object> result = syncService.testConnection(id);
        return ApiResult.success("连接成功", result);
    }

    @PostMapping("/{id}/execute")
    public ApiResult<Void> execute(@PathVariable Long id) {
        syncService.executeSync(id);
        return ApiResult.success("已触发执行", null);
    }

    @GetMapping("/logs")
    public ApiResult<PageResult<Map<String, Object>>> logs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long taskId) {
        Page<SyncLog> page = syncService.getLogPage(pageNum, pageSize, taskId);
        List<Map<String, Object>> rows = page.getRecords().stream()
                .map(this::toLogMap)
                .collect(Collectors.toList());
        return ApiResult.success(new PageResult<>(rows, page.getTotal(), (long) pageNum, (long) pageSize));
    }

    private Map<String, Object> toTaskMap(SyncTask task) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", task.getId());
        map.put("name", task.getTaskName());
        map.put("taskName", task.getTaskName());
        map.put("externalSystem", task.getSystemName());
        map.put("systemName", task.getSystemName());
        map.put("endpoint", task.getApiUrl());
        map.put("apiUrl", task.getApiUrl());
        map.put("authType", task.getAuthType());
        map.put("syncType", task.getSyncType());
        map.put("triggerType", task.getTriggerType());
        map.put("cron", task.getCronExpr());
        map.put("cronExpr", task.getCronExpr());
        map.put("status", task.getStatus());
        map.put("lastRunAt", task.getLastSyncTime() != null ? task.getLastSyncTime().toString().replace("T", " ") : null);
        map.put("lastSyncTime", task.getLastSyncTime() != null ? task.getLastSyncTime().toString().replace("T", " ") : null);
        map.put("mappings", List.of());
        return map;
    }

    private Map<String, Object> toLogMap(SyncLog log) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", log.getId());
        map.put("taskId", log.getTaskId());
        map.put("taskName", log.getTaskName());
        map.put("startAt", log.getStartTime() != null ? log.getStartTime().toString().replace("T", " ") : null);
        map.put("endAt", log.getEndTime() != null ? log.getEndTime().toString().replace("T", " ") : null);
        map.put("startTime", log.getStartTime() != null ? log.getStartTime().toString().replace("T", " ") : null);
        map.put("endTime", log.getEndTime() != null ? log.getEndTime().toString().replace("T", " ") : null);
        map.put("count", log.getSyncCount());
        map.put("syncCount", log.getSyncCount());
        map.put("successCount", log.getSuccessCount());
        map.put("failCount", log.getFailCount());
        map.put("status", log.getStatus());
        map.put("errorMessage", log.getErrorMessage());
        return map;
    }

    private SyncTask toTask(Map<String, Object> map) {
        SyncTask task = new SyncTask();
        if (map.get("id") != null) {
            task.setId(((Number) map.get("id")).longValue());
        }
        task.setTaskName((String) map.get("taskName"));
        task.setSystemName((String) map.get("systemName"));
        task.setApiUrl((String) map.get("apiUrl"));
        task.setAuthType((String) map.get("authType"));
        task.setSyncType((String) map.get("syncType"));
        task.setTriggerType((String) map.get("triggerType"));
        task.setCronExpr((String) map.get("cronExpr"));
        task.setStatus((String) map.get("status"));
        return task;
    }
}
