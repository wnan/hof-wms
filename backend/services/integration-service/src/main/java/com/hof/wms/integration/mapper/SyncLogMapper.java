package com.hof.wms.integration.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hof.wms.integration.entity.SyncLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SyncLogMapper extends BaseMapper<SyncLog> {
}
