-- 广告组合(Portfolio)信息表
CREATE TABLE IF NOT EXISTS sf_api.portfolio_info (
    portfolio_id VARCHAR(64) NOT NULL,
    shop_id VARCHAR(64) NOT NULL,
    name VARCHAR(256),
    serving_status VARCHAR(32),
    in_budget VARCHAR(32),
    amount VARCHAR(64),
    policy VARCHAR(32),
    start_date VARCHAR(32),
    end_date VARCHAR(32),
    creation_date VARCHAR(32),
    last_updated_date VARCHAR(32),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_portfolio_info PRIMARY KEY (portfolio_id, shop_id)
);

COMMENT ON TABLE sf_api.portfolio_info IS '广告组合(Portfolio)信息表，每次导入全覆盖替换';
COMMENT ON COLUMN sf_api.portfolio_info.portfolio_id IS '广告组合ID';
COMMENT ON COLUMN sf_api.portfolio_info.shop_id IS '店铺ID';
COMMENT ON COLUMN sf_api.portfolio_info.name IS '广告组合名称';
COMMENT ON COLUMN sf_api.portfolio_info.serving_status IS '服务状态';
COMMENT ON COLUMN sf_api.portfolio_info.in_budget IS '是否在预算内';
COMMENT ON COLUMN sf_api.portfolio_info.amount IS '预算金额';
COMMENT ON COLUMN sf_api.portfolio_info.policy IS '预算策略';
COMMENT ON COLUMN sf_api.portfolio_info.start_date IS '开始日期';
COMMENT ON COLUMN sf_api.portfolio_info.end_date IS '结束日期';
COMMENT ON COLUMN sf_api.portfolio_info.creation_date IS '创建日期';
COMMENT ON COLUMN sf_api.portfolio_info.last_updated_date IS '最后更新日期';

CREATE INDEX IF NOT EXISTS idx_portfolio_info_shop ON sf_api.portfolio_info (shop_id);

-- 初始化Portfolio导入任务
INSERT INTO integration.sync_task (id, task_name, system_name, api_url, auth_type, sync_type, trigger_type, cron_expr, status, params, params_class, enabled, description, created_at, updated_at)
VALUES (
    1005, 'portfolio_import', 'SellFox', 'https://openapi.sellfox.com', 'hmac', 'PORTFOLIO', 'CRON', '0 0 3 * * ?', 'active',
    '{"pageSize": "100"}'::jsonb, 'com.hof.wms.integration.model.PortfolioImportParams', TRUE, '广告组合(Portfolio)信息全量导入，每天凌晨3点执行', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
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
