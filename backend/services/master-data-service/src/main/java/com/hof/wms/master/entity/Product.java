package com.hof.wms.master.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value = "product", schema = "master")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("sku_code")
    private String skuCode;

    @TableField("product_name")
    private String productName;

    @TableField("category_name")
    private String categoryName;

    private String brand;

    private String unit;

    private String spec;

    private String barcode;

    @TableField("cost_price")
    private BigDecimal costPrice;

    @TableField("sale_price")
    private BigDecimal salePrice;

    private BigDecimal weight;

    private BigDecimal volume;

    @TableField("supplier_name")
    private String supplierName;

    @TableField("safety_stock")
    private BigDecimal safetyStock;

    private String status;

    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
