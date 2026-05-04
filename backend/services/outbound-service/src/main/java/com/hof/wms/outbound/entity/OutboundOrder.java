package com.hof.wms.outbound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "outbound_order", schema = "outbound")
public class OutboundOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("customer_name")
    private String customerName;

    @TableField("order_type")
    private String orderType;

    private String status;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<OutboundOrderItem> items;
}
