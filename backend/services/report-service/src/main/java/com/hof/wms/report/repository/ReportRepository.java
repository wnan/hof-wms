package com.hof.wms.report.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hof.wms.report.entity.ReportTask;
import com.hof.wms.report.mapper.ReportTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final ReportTaskMapper taskMapper;

    public ReportTask findById(Long id) {
        return taskMapper.selectById(id);
    }

    public ReportTask save(ReportTask task) {
        if (task.getId() == null) {
            task.setCreatedAt(LocalDateTime.now());
            taskMapper.insert(task);
        } else {
            taskMapper.updateById(task);
        }
        return task;
    }
}
