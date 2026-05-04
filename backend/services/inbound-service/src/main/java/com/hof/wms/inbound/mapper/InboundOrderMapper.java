package com.hof.wms.inbound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hof.wms.inbound.entity.InboundOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InboundOrderMapper extends BaseMapper<InboundOrder> {
}
