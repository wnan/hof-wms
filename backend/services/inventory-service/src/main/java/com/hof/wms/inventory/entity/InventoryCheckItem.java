package com.hof.wms.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "inventory_check_item", schema = "inventory")
public class InventoryCheckItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("check_order_id")
    private Long checkOrderId;

    @TableField("sku_code")
    private String skuCode;

    @TableField("product_name")
    private String productName;

    @TableField("system_qty")
    private BigDecimal systemQty;

    @TableField("actual_qty")
    private BigDecimal actualQty;

    @TableField("diff_qty")
    private BigDecimal diffQty;
}
