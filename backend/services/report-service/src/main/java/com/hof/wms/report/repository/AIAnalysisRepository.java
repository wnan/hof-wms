package com.hof.wms.report.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.report.entity.AIAnalysisRecord;
import com.hof.wms.report.mapper.AIAnalysisRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class AIAnalysisRepository {

    private final AIAnalysisRecordMapper recordMapper;

    public IPage<AIAnalysisRecord> findPage(int pageNum, int pageSize) {
        Page<AIAnalysisRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AIAnalysisRecord> wrapper = new LambdaQueryWrapper();
        wrapper.orderByDesc(AIAnalysisRecord::getCreatedAt);
        return recordMapper.selectPage(page, wrapper);
    }

    public AIAnalysisRecord findById(Long id) {
        return recordMapper.selectById(id);
    }

    public AIAnalysisRecord save(AIAnalysisRecord record) {
        if (record.getId() == null) {
            record.setCreatedAt(LocalDateTime.now());
            recordMapper.insert(record);
        } else {
            recordMapper.updateById(record);
        }
        return record;
    }
}
