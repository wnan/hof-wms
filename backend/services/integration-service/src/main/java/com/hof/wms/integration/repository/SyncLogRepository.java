package com.hof.wms.integration.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.integration.entity.SyncLog;
import com.hof.wms.integration.mapper.SyncLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SyncLogRepository {

    private final SyncLogMapper logMapper;

    public Page<SyncLog> findPage(int pageNum, int pageSize, Long taskId) {
        Page<SyncLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SyncLog> wrapper = new LambdaQueryWrapper();
        if (taskId != null) {
            wrapper.eq(SyncLog::getTaskId, taskId);
        }
        wrapper.orderByDesc(SyncLog::getStartTime);
        return logMapper.selectPage(page, wrapper);
    }

    public SyncLog findById(Long id) {
        return logMapper.selectById(id);
    }

    public SyncLog save(SyncLog log) {
        if (log.getId() == null) {
            logMapper.insert(log);
        } else {
            logMapper.updateById(log);
        }
        return log;
    }
}
