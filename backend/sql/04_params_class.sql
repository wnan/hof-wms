-- ========================================
-- 为 sync_task 添加 params_class 列
-- ========================================

ALTER TABLE integration.sync_task ADD COLUMN IF NOT EXISTS params_class VARCHAR(255);

COMMENT ON COLUMN integration.sync_task.params_class IS '参数类全限定名，用于将params JSON反序列化为具体对象';

-- 更新已有任务数据，设置 params_class
UPDATE integration.sync_task SET params_class = 'com.hof.wms.integration.model.ShopInfoImportParams'
WHERE task_name = 'shop_info_import';

UPDATE integration.sync_task SET params_class = 'com.hof.wms.integration.model.AdCampaignImportParams'
WHERE task_name = 'ad_campaign_import';
