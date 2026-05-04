package com.hof.wms.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "inventory_check_order", schema = "inventory")
public class InventoryCheckOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("check_no")
    private String checkNo;

    @TableField("warehouse_name")
    private String warehouseName;

    private String status;

    @TableField("check_date")
    private LocalDate checkDate;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<InventoryCheckItem> items;
}
