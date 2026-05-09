package com.hof.wms.integration.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hof.wms.integration.entity.AdCampaignReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdCampaignReportMapper extends BaseMapper<AdCampaignReport> {

    @Insert("INSERT INTO sf_api.ad_campaign_report " +
            "(shop_id, shop_name, report_type_code, report_date, campaign_name, targeting_type, campaign_status, " +
            "impressions, clicks, spend, cpc, ad_sales, advertised_product_sales, " +
            "other_product_ad_sales, ctr, conversion_rate, acos, roas, " +
            "ad_orders, advertised_product_orders, other_product_ad_orders, " +
            "ad_units, advertised_product_units, other_product_ad_units, " +
            "campaign_start_date, campaign_end_date, portfolio_id, campaign_id, created_at) " +
            "VALUES (#{shopId}, #{shopName}, #{reportTypeCode}, #{reportDate}, #{campaignName}, #{targetingType}, #{campaignStatus}, " +
            "#{impressions}, #{clicks}, #{spend}, #{cpc}, #{adSales}, #{advertisedProductSales}, " +
            "#{otherProductAdSales}, #{ctr}, #{conversionRate}, #{acos}, #{roas}, " +
            "#{adOrders}, #{advertisedProductOrders}, #{otherProductAdOrders}, " +
            "#{adUnits}, #{advertisedProductUnits}, #{otherProductAdUnits}, " +
            "#{campaignStartDate}, #{campaignEndDate}, #{portfolioId}, #{campaignId}, CURRENT_TIMESTAMP) " +
            "ON CONFLICT (shop_id, report_type_code, campaign_name, report_date) DO UPDATE SET " +
            "shop_name = EXCLUDED.shop_name, targeting_type = EXCLUDED.targeting_type, " +
            "campaign_status = EXCLUDED.campaign_status, impressions = EXCLUDED.impressions, " +
            "clicks = EXCLUDED.clicks, spend = EXCLUDED.spend, cpc = EXCLUDED.cpc, " +
            "ad_sales = EXCLUDED.ad_sales, advertised_product_sales = EXCLUDED.advertised_product_sales, " +
            "other_product_ad_sales = EXCLUDED.other_product_ad_sales, ctr = EXCLUDED.ctr, " +
            "conversion_rate = EXCLUDED.conversion_rate, acos = EXCLUDED.acos, roas = EXCLUDED.roas, " +
            "ad_orders = EXCLUDED.ad_orders, advertised_product_orders = EXCLUDED.advertised_product_orders, " +
            "other_product_ad_orders = EXCLUDED.other_product_ad_orders, " +
            "ad_units = EXCLUDED.ad_units, advertised_product_units = EXCLUDED.advertised_product_units, " +
            "other_product_ad_units = EXCLUDED.other_product_ad_units, " +
            "campaign_start_date = EXCLUDED.campaign_start_date, campaign_end_date = EXCLUDED.campaign_end_date, " +
            "portfolio_id = EXCLUDED.portfolio_id, campaign_id = EXCLUDED.campaign_id")
    int upsert(AdCampaignReport report);
}
