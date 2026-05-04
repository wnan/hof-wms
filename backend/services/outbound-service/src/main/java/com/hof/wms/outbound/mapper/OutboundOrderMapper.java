package com.hof.wms.outbound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hof.wms.outbound.entity.OutboundOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutboundOrderMapper extends BaseMapper<OutboundOrder> {
}
