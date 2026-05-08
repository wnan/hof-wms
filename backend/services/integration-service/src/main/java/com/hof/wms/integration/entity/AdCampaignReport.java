package com.hof.wms.integration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 广告活动报告实体
 * 对应 sf_api.ad_campaign_report 表
 */
@Data
@TableName(value = "ad_campaign_report", schema = "sf_api")
public class AdCampaignReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("shop_id")
    private String shopId;

    @TableField("shop_name")
    private String shopName;

    @TableField("report_date")
    private LocalDate reportDate;

    @TableField("campaign_name")
    private String campaignName;

    @TableField("targeting_type")
    private String targetingType;

    @TableField("campaign_status")
    private String campaignStatus;

    private Long impressions;

    private Long clicks;

    private BigDecimal spend;

    private BigDecimal cpc;

    @TableField("ad_sales")
    private BigDecimal adSales;

    @TableField("advertised_product_sales")
    private BigDecimal advertisedProductSales;

    @TableField("other_product_ad_sales")
    private BigDecimal otherProductAdSales;

    private BigDecimal ctr;

    @TableField("conversion_rate")
    private BigDecimal conversionRate;

    private BigDecimal acos;

    private BigDecimal roas;

    @TableField("ad_orders")
    private Long adOrders;

    @TableField("advertised_product_orders")
    private Long advertisedProductOrders;

    @TableField("other_product_ad_orders")
    private Long otherProductAdOrders;

    @TableField("ad_units")
    private Long adUnits;

    @TableField("advertised_product_units")
    private Long advertisedProductUnits;

    @TableField("other_product_ad_units")
    private Long otherProductAdUnits;

    @TableField("campaign_start_date")
    private LocalDate campaignStartDate;

    @TableField("campaign_end_date")
    private LocalDate campaignEndDate;

    @TableField("portfolio_id")
    private String portfolioId;

    @TableField("campaign_id")
    private String campaignId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
