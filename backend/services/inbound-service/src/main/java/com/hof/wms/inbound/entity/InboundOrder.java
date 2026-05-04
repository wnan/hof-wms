package com.hof.wms.inbound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "inbound_order", schema = "inbound")
public class InboundOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("supplier_name")
    private String supplierName;

    @TableField("order_type")
    private String orderType;

    private String status;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<InboundOrderItem> items;
}
