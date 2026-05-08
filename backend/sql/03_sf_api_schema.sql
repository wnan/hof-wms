-- ========================================
-- SellFox API 数据导入 DDL
-- ========================================

CREATE SCHEMA IF NOT EXISTS sf_api;

-- 店铺信息表（全覆盖导入）
CREATE TABLE IF NOT EXISTS sf_api.shop_info (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128),
    seller_id VARCHAR(64),
    region VARCHAR(32),
    marketplace_id VARCHAR(64),
    ad_status VARCHAR(32),
    status VARCHAR(32),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sf_api.shop_info IS '店铺信息表，每次导入全覆盖替换';
COMMENT ON COLUMN sf_api.shop_info.ad_status IS '广告状态';
COMMENT ON COLUMN sf_api.shop_info.status IS '店铺状态';

-- 广告活动报告表
CREATE TABLE IF NOT EXISTS sf_api.ad_campaign_report (
    id BIGSERIAL PRIMARY KEY,
    shop_id VARCHAR(64) NOT NULL,
    shop_name VARCHAR(128),
    report_date DATE NOT NULL,
    campaign_name VARCHAR(256),
    targeting_type VARCHAR(32),
    campaign_status VARCHAR(32),
    impressions BIGINT DEFAULT 0,
    clicks BIGINT DEFAULT 0,
    spend NUMERIC(18,4) DEFAULT 0,
    cpc NUMERIC(18,4) DEFAULT 0,
    ad_sales NUMERIC(18,4) DEFAULT 0,
    advertised_product_sales NUMERIC(18,4) DEFAULT 0,
    other_product_ad_sales NUMERIC(18,4) DEFAULT 0,
    ctr NUMERIC(18,6) DEFAULT 0,
    conversion_rate NUMERIC(18,6) DEFAULT 0,
    acos NUMERIC(18,6) DEFAULT 0,
    roas NUMERIC(18,6) DEFAULT 0,
    ad_orders BIGINT DEFAULT 0,
    advertised_product_orders BIGINT DEFAULT 0,
    other_product_ad_orders BIGINT DEFAULT 0,
    ad_units BIGINT DEFAULT 0,
    advertised_product_units BIGINT DEFAULT 0,
    other_product_ad_units BIGINT DEFAULT 0,
    campaign_start_date DATE,
    campaign_end_date DATE,
    portfolio_id VARCHAR(64),
    campaign_id VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ad_campaign_report UNIQUE (shop_id, campaign_name, report_date)
);

COMMENT ON TABLE sf_api.ad_campaign_report IS 'SP广告活动报告数据';
COMMENT ON COLUMN sf_api.ad_campaign_report.targeting_type IS '定位类型（自动/手动）';
COMMENT ON COLUMN sf_api.ad_campaign_report.campaign_status IS '广告活动运行状态';
COMMENT ON COLUMN sf_api.ad_campaign_report.spend IS '广告花费（美元）';
COMMENT ON COLUMN sf_api.ad_campaign_report.cpc IS 'CPC单次点击费用（美元）';
COMMENT ON COLUMN sf_api.ad_campaign_report.ad_sales IS '广告销售额（美元）';
COMMENT ON COLUMN sf_api.ad_campaign_report.advertised_product_sales IS '本广告产品销售额（美元）';
COMMENT ON COLUMN sf_api.ad_campaign_report.other_product_ad_sales IS '其他产品广告销售额（美元）';
COMMENT ON COLUMN sf_api.ad_campaign_report.ctr IS '广告点击率（1.87%存为0.018700）';
COMMENT ON COLUMN sf_api.ad_campaign_report.conversion_rate IS '广告转化率';
COMMENT ON COLUMN sf_api.ad_campaign_report.acos IS 'ACoS广告销售成本比';
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

CREATE INDEX IF NOT EXISTS idx_ad_campaign_report_date ON sf_api.ad_campaign_report (report_date);
CREATE INDEX IF NOT EXISTS idx_ad_campaign_report_shop ON sf_api.ad_campaign_report (shop_id);

-- ========================================
-- 扩展 integration.sync_task 表
-- 为SellFox导入任务增加字段
-- ========================================

-- 补充默认值（原表 created_at / updated_at 无默认值）
ALTER TABLE integration.sync_task ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE integration.sync_task ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE integration.sync_task ADD COLUMN IF NOT EXISTS params JSONB;
ALTER TABLE integration.sync_task ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE integration.sync_task ADD COLUMN IF NOT EXISTS last_execute_status VARCHAR(32);
ALTER TABLE integration.sync_task ADD COLUMN IF NOT EXISTS last_execute_message VARCHAR(500);
ALTER TABLE integration.sync_task ADD COLUMN IF NOT EXISTS description VARCHAR(500);
ALTER TABLE integration.sync_task ADD COLUMN IF NOT EXISTS params_class VARCHAR(255);

COMMENT ON COLUMN integration.sync_task.params IS '任务参数(JSONB)，值支持SpEL表达式';
COMMENT ON COLUMN integration.sync_task.params_class IS '参数类全限定名，用于将params JSON反序列化为具体对象';
COMMENT ON COLUMN integration.sync_task.enabled IS '是否启用';
COMMENT ON COLUMN integration.sync_task.last_execute_status IS '最近执行状态(SUCCESS/FAILED)';
COMMENT ON COLUMN integration.sync_task.last_execute_message IS '最近执行消息';

-- 给task_name加唯一约束以支持UPSERT去重
ALTER TABLE integration.sync_task ADD CONSTRAINT uk_sync_task_task_name UNIQUE (task_name);

-- 初始化SellFox导入任务数据（在sync_task中，按task_name去重）
INSERT INTO integration.sync_task (id, task_name, system_name, api_url, auth_type, sync_type, trigger_type, cron_expr, status, params, params_class, enabled, description, created_at, updated_at)
VALUES (
    1001, 'shop_info_import', 'SellFox', 'https://openapi.sellfox.com', 'hmac', 'SHOP_INFO', 'CRON', '0 0 2 * * ?', 'active',
    '{"pageSize": "200"}'::jsonb, 'com.hof.wms.integration.model.ShopInfoImportParams', TRUE, '店铺信息全量导入，每天凌晨2点执行', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) ON CONFLICT (task_name) DO UPDATE SET
    system_name = EXCLUDED.system_name,
    api_url = EXCLUDED.api_url,
    auth_type = EXCLUDED.auth_type,
    sync_type = EXCLUDED.sync_type,
    trigger_type = EXCLUDED.trigger_type,
    cron_expr = EXCLUDED.cron_expr,
    params = EXCLUDED.params,
    params_class = EXCLUDED.params_class,
    description = EXCLUDED.description,
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO integration.sync_task (id, task_name, system_name, api_url, auth_type, sync_type, trigger_type, cron_expr, status, params, params_class, enabled, description, created_at, updated_at)
VALUES (
    1002, 'ad_campaign_import', 'SellFox', 'https://openapi.sellfox.com', 'hmac', 'AD_CAMPAIGN', 'CRON', '0 30 2 * * ?', 'active',
    '{
        "adTypeCode": "sp",
        "reportTypeCode": "adCampaignReport",
        "timeUnit": "daily",
        "startDateExpr": "T(java.time.LocalDate).now().minusDays(1).toString()",
        "endDateExpr": "T(java.time.LocalDate).now().toString()",
        "deleteDateExpr": "T(java.time.LocalDate).now().minusDays(1).toString()"
    }'::jsonb, 'com.hof.wms.integration.model.AdCampaignImportParams', TRUE, '广告活动数据导入，每天凌晨2:30执行', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) ON CONFLICT (task_name) DO UPDATE SET
    system_name = EXCLUDED.system_name,
    api_url = EXCLUDED.api_url,
    auth_type = EXCLUDED.auth_type,
    sync_type = EXCLUDED.sync_type,
    trigger_type = EXCLUDED.trigger_type,
    cron_expr = EXCLUDED.cron_expr,
    params = EXCLUDED.params,
    params_class = EXCLUDED.params_class,
    description = EXCLUDED.description,
    updated_at = CURRENT_TIMESTAMP;
