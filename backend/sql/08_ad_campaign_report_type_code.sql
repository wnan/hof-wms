-- 广告活动报告表增加 report_type_code 列
ALTER TABLE sf_api.ad_campaign_report
    ADD COLUMN IF NOT EXISTS report_type_code VARCHAR(64) NOT NULL DEFAULT 'adCampaignReport';

COMMENT ON COLUMN sf_api.ad_campaign_report.report_type_code IS '报告类型编码（adCampaignReport, adGroupReport 等）';

-- 删除旧唯一约束，建立包含 report_type_code 的新唯一约束
ALTER TABLE sf_api.ad_campaign_report DROP CONSTRAINT IF EXISTS uk_ad_campaign_report;
ALTER TABLE sf_api.ad_campaign_report ADD CONSTRAINT uk_ad_campaign_report
    UNIQUE (shop_id, report_type_code, campaign_name, report_date);

-- 增加 report_type_code 索引
CREATE INDEX IF NOT EXISTS idx_ad_campaign_report_type ON sf_api.ad_campaign_report (report_type_code);
