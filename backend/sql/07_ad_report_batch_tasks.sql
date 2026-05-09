-- 广告报告批量参数任务迁移
-- 将旧的单一 ad_campaign_import 任务替换为按广告类型分组的3个批量任务

-- 删除旧的单一广告任务
DELETE FROM integration.sync_task WHERE task_name = 'ad_campaign_import';

-- SP 广告报告导入任务 (8种报告类型)
INSERT INTO integration.sync_task (
    id, task_name, system_name, api_url, auth_type, sync_type,
    trigger_type, cron_expr, status, params, params_class,
    enabled, description, created_at, updated_at
) VALUES (
    1002, 'ad_report_import_sp', 'SellFox',
    'https://openapi.sellfox.com', 'hmac', 'AD_CAMPAIGN',
    'CRON', '0 30 2 * * ?', 'active',
    '{
        "timeUnit": "daily",
        "adTypeCode": "sp",
        "reportTypeCodes": [
            "adCampaignReport", "adGroupReport", "adProductReport",
            "adSpaceReport", "adTargeringReport", "adSearchTermReport",
            "adPurchasedItemReport", "amazonBusinessReport"
        ],
        "startDateExpr": "T(java.time.LocalDate).now().minusDays(1).toString()",
        "endDateExpr": "T(java.time.LocalDate).now().toString()"
    }'::jsonb,
    'com.hof.wms.integration.model.AdCampaignImportParams',
    TRUE, 'SP广告报告批量导入(8种报告类型)',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) ON CONFLICT (task_name) DO UPDATE SET
    params = EXCLUDED.params,
    params_class = EXCLUDED.params_class,
    description = EXCLUDED.description,
    updated_at = CURRENT_TIMESTAMP;

-- SB 广告报告导入任务 (7种报告类型)
INSERT INTO integration.sync_task (
    id, task_name, system_name, api_url, auth_type, sync_type,
    trigger_type, cron_expr, status, params, params_class,
    enabled, description, created_at, updated_at
) VALUES (
    1003, 'ad_report_import_sb', 'SellFox',
    'https://openapi.sellfox.com', 'hmac', 'AD_CAMPAIGN',
    'CRON', '0 0 3 * * ?', 'active',
    '{
        "timeUnit": "daily",
        "adTypeCode": "sb",
        "reportTypeCodes": [
            "adCampaignReport", "adGroupReport", "adProductReport",
            "adSpaceReport", "adTargeringReport", "adSearchTermReport",
            "adPurchasedItemReport"
        ],
        "startDateExpr": "T(java.time.LocalDate).now().minusDays(1).toString()",
        "endDateExpr": "T(java.time.LocalDate).now().toString()"
    }'::jsonb,
    'com.hof.wms.integration.model.AdCampaignImportParams',
    TRUE, 'SB广告报告批量导入(7种报告类型)',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) ON CONFLICT (task_name) DO UPDATE SET
    params = EXCLUDED.params,
    params_class = EXCLUDED.params_class,
    description = EXCLUDED.description,
    updated_at = CURRENT_TIMESTAMP;

-- SD 广告报告导入任务 (6种报告类型)
INSERT INTO integration.sync_task (
    id, task_name, system_name, api_url, auth_type, sync_type,
    trigger_type, cron_expr, status, params, params_class,
    enabled, description, created_at, updated_at
) VALUES (
    1004, 'ad_report_import_sd', 'SellFox',
    'https://openapi.sellfox.com', 'hmac', 'AD_CAMPAIGN',
    'CRON', '0 30 3 * * ?', 'active',
    '{
        "timeUnit": "daily",
        "adTypeCode": "sd",
        "reportTypeCodes": [
            "adCampaignReport", "adGroupReport", "adProductReport",
            "adPurchasedItemReport", "adCampaignMatchedTargetReport",
            "sdTargetListReport"
        ],
        "startDateExpr": "T(java.time.LocalDate).now().minusDays(1).toString()",
        "endDateExpr": "T(java.time.LocalDate).now().toString()"
    }'::jsonb,
    'com.hof.wms.integration.model.AdCampaignImportParams',
    TRUE, 'SD广告报告批量导入(6种报告类型)',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) ON CONFLICT (task_name) DO UPDATE SET
    params = EXCLUDED.params,
    params_class = EXCLUDED.params_class,
    description = EXCLUDED.description,
    updated_at = CURRENT_TIMESTAMP;
