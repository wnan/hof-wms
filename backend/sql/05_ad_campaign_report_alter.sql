-- ========================================
-- 重构 ad_campaign_report 表
-- 增加新列，删除旧列 portfolio_name
-- ========================================

-- 新增列
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS targeting_type VARCHAR(32);
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS roas NUMERIC(18,6) DEFAULT 0;
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS ad_orders BIGINT DEFAULT 0;
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS advertised_product_orders BIGINT DEFAULT 0;
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS other_product_ad_orders BIGINT DEFAULT 0;
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS ad_units BIGINT DEFAULT 0;
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS advertised_product_units BIGINT DEFAULT 0;
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS other_product_ad_units BIGINT DEFAULT 0;
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS campaign_start_date DATE;
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS campaign_end_date DATE;
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS portfolio_id VARCHAR(64);
ALTER TABLE sf_api.ad_campaign_report ADD COLUMN IF NOT EXISTS campaign_id VARCHAR(64);

-- 删除旧列 portfolio_name（如不需要可删除，数据已迁移到 portfolio_id）
-- ALTER TABLE sf_api.ad_campaign_report DROP COLUMN IF EXISTS portfolio_name;

-- 新增注释
COMMENT ON COLUMN sf_api.ad_campaign_report.targeting_type IS '定位类型（自动/手动）';
COMMENT ON COLUMN sf_api.ad_campaign_report.roas IS 'ROAS广告投入产出比';
COMMENT ON COLUMN sf_api.ad_campaign_report.ad_orders IS '广告订单量';
COMMENT ON COLUMN sf_api.ad_campaign_report.advertised_product_orders IS '本广告产品订单量';
COMMENT ON COLUMN sf_api.ad_campaign_report.other_product_ad_orders IS '其他产品广告订单量';
COMMENT ON COLUMN sf_api.ad_campaign_report.ad_units IS '广告销量';
COMMENT ON COLUMN sf_api.ad_campaign_report.advertised_product_units IS '本广告产品销量';
COMMENT ON COLUMN sf_api.ad_campaign_report.other_product_ad_units IS '其他产品广告销量';
COMMENT ON COLUMN sf_api.ad_campaign_report.campaign_start_date IS '广告活动开始时间';
COMMENT ON COLUMN sf_api.ad_campaign_report.campaign_end_date IS '广告活动结束时间';
COMMENT ON COLUMN sf_api.ad_campaign_report.portfolio_id IS '广告组合ID';
COMMENT ON COLUMN sf_api.ad_campaign_report.campaign_id IS '广告活动ID';

-- 已有CTR等百分比数据如果存的是原始值（如1.87而非0.0187），需要修正
-- UPDATE sf_api.ad_campaign_report SET ctr = ctr / 100 WHERE ctr > 1;
-- UPDATE sf_api.ad_campaign_report SET conversion_rate = conversion_rate / 100 WHERE conversion_rate > 1;
-- UPDATE sf_api.ad_campaign_report SET acos = acos / 100 WHERE acos > 1;
