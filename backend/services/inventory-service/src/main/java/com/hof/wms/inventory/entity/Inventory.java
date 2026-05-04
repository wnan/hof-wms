package com.hof.wms.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value = "inventory", schema = "inventory")
public class Inventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("sku_code")
    private String skuCode;

    @TableField("product_name")
    private String productName;

    @TableField("category_name")
    private String categoryName;

    @TableField("warehouse_name")
    private String warehouseName;

    @TableField("available_qty")
    private BigDecimal availableQty;

    @TableField("locked_qty")
    private BigDecimal lockedQty;

    @TableField("total_qty")
    private BigDecimal totalQty;

    @TableField("safety_stock")
    private BigDecimal safetyStock;

    @Version
    private Integer version;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
