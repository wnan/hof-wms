-- 广告活动报告表增加 ad_type_code 列
ALTER TABLE sf_api.ad_campaign_report
    ADD COLUMN IF NOT EXISTS ad_type_code VARCHAR(32) NOT NULL DEFAULT 'sp';

COMMENT ON COLUMN sf_api.ad_campaign_report.ad_type_code IS '广告类型编码（sp/sb/sd）';

-- 删除旧唯一约束，建立包含 ad_type_code 的新唯一约束
ALTER TABLE sf_api.ad_campaign_report DROP CONSTRAINT IF EXISTS uk_ad_campaign_report;
ALTER TABLE sf_api.ad_campaign_report ADD CONSTRAINT uk_ad_campaign_report
    UNIQUE (shop_id, report_type_code, ad_type_code, campaign_name, report_date);

-- 增加 ad_type_code 索引
CREATE INDEX IF NOT EXISTS idx_ad_campaign_report_ad_type ON sf_api.ad_campaign_report (ad_type_code);
