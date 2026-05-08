package com.hof.wms.integration.controller;

import com.hof.wms.common.result.ApiResult;
import com.hof.wms.integration.entity.SyncTask;
import com.hof.wms.integration.service.TaskScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * SellFox数据导入管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/sf-import")
@RequiredArgsConstructor
public class SfImportController {

    private final TaskScheduleService taskScheduleService;

    @GetMapping("/tasks")
    public ApiResult<List<SyncTask>> getTasks() {
        return ApiResult.success(taskScheduleService.getAllTasks());
    }

    @PutMapping("/tasks/{id}")
    public ApiResult<Void> updateTask(@PathVariable Long id, @RequestBody SyncTask config) {
        try {
            config.setId(id);
            taskScheduleService.updateTask(config);
            return ApiResult.success(null);
        } catch (Exception e) {
            log.error("更新任务配置失败: {}", e.getMessage(), e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @PostMapping("/tasks/{id}/execute")
    public ApiResult<Void> executeTask(@PathVariable Long id) {
        try {
            taskScheduleService.executeTask(id);
            return ApiResult.success(null);
        } catch (Exception e) {
            log.error("触发任务失败: {}", e.getMessage(), e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @GetMapping("/tasks/{id}/status")
    public ApiResult<SyncTask> getTaskStatus(@PathVariable Long id) {
        SyncTask task = taskScheduleService.getTaskById(id);
        if (task == null) {
            return ApiResult.fail("任务不存在");
        }
        return ApiResult.success(task);
    }

    @PatchMapping("/tasks/{id}/toggle")
    public ApiResult<Map<String, Object>> toggleTask(@PathVariable Long id) {
        try {
            taskScheduleService.toggleTask(id);
            SyncTask task = taskScheduleService.getTaskById(id);
            return ApiResult.success(Map.of("enabled", task.getEnabled()));
        } catch (Exception e) {
            log.error("切换任务状态失败: {}", e.getMessage(), e);
            return ApiResult.fail(e.getMessage());
        }
    }
}
