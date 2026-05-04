package com.hof.wms.integration.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hof.wms.integration.entity.SyncTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SyncTaskMapper extends BaseMapper<SyncTask> {
}
