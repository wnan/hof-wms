package com.hof.wms.outbound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hof.wms.outbound.entity.OutboundOrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutboundOrderItemMapper extends BaseMapper<OutboundOrderItem> {
}
