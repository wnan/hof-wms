package com.hof.wms.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hof.wms.inventory.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {
}
