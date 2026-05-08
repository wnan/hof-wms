package com.hof.wms.integration.service;

import com.hof.wms.integration.config.QuartzConfig;
import com.hof.wms.integration.entity.SyncTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时调度管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskScheduleService {

    private final QuartzConfig quartzConfig;
    private final SfImportService sfImportService;

    public List<SyncTask> getAllTasks() {
        return sfImportService.getAllTaskConfigs();
    }

    public SyncTask getTaskById(Long id) {
        return sfImportService.getTaskConfigById(id);
    }

    public void updateTask(SyncTask task) throws SchedulerException {
        sfImportService.updateTaskConfig(task);

        if (Boolean.TRUE.equals(task.getEnabled())) {
            quartzConfig.scheduleTask(task);
        } else {
            quartzConfig.unscheduleTask(task.getTaskName());
        }
    }

    public void executeTask(Long id) throws SchedulerException {
        SyncTask task = sfImportService.getTaskConfigById(id);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + id);
        }

        JobKey jobKey = JobKey.jobKey(task.getTaskName() + "_job", "import_tasks");
        quartzConfig.getScheduler().triggerJob(jobKey);
    }

    public void toggleTask(Long id) throws SchedulerException {
        SyncTask task = sfImportService.getTaskConfigById(id);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + id);
        }

        task.setEnabled(!Boolean.TRUE.equals(task.getEnabled()));
        sfImportService.updateTaskConfig(task);

        if (Boolean.TRUE.equals(task.getEnabled())) {
            quartzConfig.scheduleTask(task);
        } else {
            quartzConfig.unscheduleTask(task.getTaskName());
        }
    }
}
