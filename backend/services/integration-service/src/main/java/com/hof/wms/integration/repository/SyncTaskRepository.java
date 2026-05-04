package com.hof.wms.integration.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.integration.entity.SyncTask;
import com.hof.wms.integration.mapper.SyncTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class SyncTaskRepository {

    private final SyncTaskMapper taskMapper;

    public Page<SyncTask> findPage(int pageNum, int pageSize, String keyword) {
        Page<SyncTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SyncTask> wrapper = new LambdaQueryWrapper();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SyncTask::getTaskName, keyword)
                    .or().like(SyncTask::getSystemName, keyword));
        }
        wrapper.orderByDesc(SyncTask::getUpdatedAt);
        return taskMapper.selectPage(page, wrapper);
    }

    public SyncTask findById(Long id) {
        return taskMapper.selectById(id);
    }

    public SyncTask save(SyncTask task) {
        if (task.getId() == null) {
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            task.setStatus("active");
            taskMapper.insert(task);
        } else {
            task.setUpdatedAt(LocalDateTime.now());
            taskMapper.updateById(task);
        }
        return task;
    }

    public void updateStatus(Long id, String status) {
        SyncTask task = findById(id);
        if (task != null) {
            task.setStatus(status);
            task.setUpdatedAt(LocalDateTime.now());
            taskMapper.updateById(task);
        }
    }

    public void updateLastSyncTime(Long id) {
        SyncTask task = findById(id);
        if (task != null) {
            task.setLastSyncTime(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            taskMapper.updateById(task);
        }
    }
}
