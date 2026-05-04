package com.hof.wms.outbound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "outbound_order_item", schema = "outbound")
public class OutboundOrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("sku_code")
    private String skuCode;

    @TableField("product_name")
    private String productName;

    private BigDecimal quantity;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    private BigDecimal amount;
}
