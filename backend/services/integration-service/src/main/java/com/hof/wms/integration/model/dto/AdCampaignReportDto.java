package com.hof.wms.integration.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * SP广告活动报告 EasyExcel 读取DTO
 * 列头严格对应Excel文件
 */
@Data
public class AdCampaignReportDto {

    @ExcelProperty("店铺")
    private String shopName;

    @ExcelProperty("日期")
    private String date;

    @ExcelProperty("广告活动")
    private String campaignName;

    @ExcelProperty("定位类型")
    private String targetingType;

    @ExcelProperty("广告花费")
    private String spend;

    @ExcelProperty("广告曝光量")
    private String impressions;

    @ExcelProperty("广告点击量")
    private String clicks;

    @ExcelProperty("CPC")
    private String cpc;

    @ExcelProperty("广告点击率")
    private String ctr;

    @ExcelProperty("广告转化率")
    private String conversionRate;

    @ExcelProperty("ACoS")
    private String acos;

    @ExcelProperty("ROAS")
    private String roas;

    @ExcelProperty("广告订单量")
    private String adOrders;

    @ExcelProperty("本广告产品订单量")
    private String advertisedProductOrders;

    @ExcelProperty("其他产品广告订单量")
    private String otherProductAdOrders;

    @ExcelProperty("广告销售额")
    private String adSales;

    @ExcelProperty("本广告产品销售额")
    private String advertisedProductSales;

    @ExcelProperty("其他产品广告销售额")
    private String otherProductAdSales;

    @ExcelProperty("广告销量")
    private String adUnits;

    @ExcelProperty("本广告产品销量")
    private String advertisedProductUnits;

    @ExcelProperty("其他产品广告销量")
    private String otherProductAdUnits;

    @ExcelProperty("广告活动开始时间")
    private String campaignStartDate;

    @ExcelProperty("广告活动结束时间")
    private String campaignEndDate;

    @ExcelProperty("广告活动运行状态")
    private String campaignStatus;

    @ExcelProperty("广告组合ID")
    private String portfolioId;

    @ExcelProperty("广告活动ID")
    private String campaignId;
}
