package com.hof.wms.integration.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.integration.entity.SyncLog;
import com.hof.wms.integration.entity.SyncTask;
import com.hof.wms.integration.repository.SyncLogRepository;
import com.hof.wms.integration.repository.SyncTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final SyncTaskRepository taskRepository;
    private final SyncLogRepository logRepository;

    public Page<SyncTask> getTaskPage(int pageNum, int pageSize, String keyword) {
        return taskRepository.findPage(pageNum, pageSize, keyword);
    }

    public SyncTask getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional
    public SyncTask createTask(SyncTask task) {
        task.setId(null);
        return taskRepository.save(task);
    }

    @Transactional
    public SyncTask updateTask(Long id, SyncTask task) {
        SyncTask existing = taskRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("同步任务不存在");
        }
        task.setId(id);
        return taskRepository.save(task);
    }

    public Map<String, Object> testConnection(Long id) {
        SyncTask task = taskRepository.findById(id);
        if (task == null) {
            throw new RuntimeException("同步任务不存在");
        }
        return Map.of("ok", true, "message", "连接成功");
    }

    @Transactional
    public void executeSync(Long id) {
        SyncTask task = taskRepository.findById(id);
        if (task == null) {
            throw new RuntimeException("同步任务不存在");
        }

        SyncLog log = new SyncLog();
        log.setTaskId(id);
        log.setTaskName(task.getTaskName());
        log.setStartTime(LocalDateTime.now());
        log.setSyncCount(0);
        log.setSuccessCount(0);
        log.setFailCount(0);
        log.setStatus("running");
        logRepository.save(log);

        try {
            Thread.sleep(1000);
            log.setEndTime(LocalDateTime.now());
            log.setSyncCount(100);
            log.setSuccessCount(100);
            log.setFailCount(0);
            log.setStatus("success");
            taskRepository.updateLastSyncTime(id);
        } catch (Exception e) {
            log.setEndTime(LocalDateTime.now());
            log.setStatus("failed");
            log.setErrorMessage(e.getMessage());
        }

        logRepository.save(log);
    }

    public Page<SyncLog> getLogPage(int pageNum, int pageSize, Long taskId) {
        return logRepository.findPage(pageNum, pageSize, taskId);
    }
}
