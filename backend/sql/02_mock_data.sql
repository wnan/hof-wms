INSERT INTO auth.sys_user (id, username, real_name, email, phone, department, status, password_hash, last_login_at, created_at, updated_at)
VALUES
    (1000, 'admin', '管理员', 'admin@wms.com', '13800000000', '技术部', 'active', 'insightowl-demo', '2026-05-03 10:00:00', '2026-01-01 10:00:00', '2026-05-03 10:00:00'),
    (1001, 'operator001', '仓库操作员', 'operator@wms.com', '13800000001', '供应链部', 'active', 'insightowl-demo', '2026-05-02 09:30:00', '2026-01-05 10:00:00', '2026-05-02 09:30:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO auth.sys_role (id, code, name, description, status, created_at)
VALUES
    (2000, 'super_admin', '超级管理员', '拥有系统全部权限', 'active', '2026-01-01 09:00:00'),
    (2001, 'warehouse_admin', '仓库管理员', '管理仓储业务', 'active', '2026-01-05 10:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO auth.sys_permission (id, parent_id, name, code, type, path, sort)
VALUES
    (3000, NULL, '仪表盘', 'dashboard', 'menu', '/dashboard', 1),
    (3001, NULL, '入库管理', 'inbound', 'menu', '/inbound/list', 2),
    (3002, NULL, '商品管理', 'sku', 'menu', '/sku/list', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO auth.sys_user_role (id, user_id, role_id)
VALUES
    (4000, 1000, 2000),
    (4001, 1001, 2001)
ON CONFLICT (id) DO NOTHING;

INSERT INTO auth.sys_role_permission (id, role_id, permission_id)
VALUES
    (5000, 2000, 3000),
    (5001, 2000, 3001),
    (5002, 2000, 3002),
    (5003, 2001, 3001)
ON CONFLICT (id) DO NOTHING;

INSERT INTO master.product_category (id, parent_id, name, sort)
VALUES
    (6000, NULL, '3C数码', 1),
    (6001, NULL, '家居日用', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO master.product (id, sku_code, product_name, category_name, brand, unit, spec, barcode, cost_price, sale_price, weight, volume, supplier_name, safety_stock, status, remark, created_at, updated_at)
VALUES
    (7000, 'SKU010001', '智能手表 Pro v1', '3C数码', '小米', '件', '规格-S / 黑', '691000000001', 199.00, 299.00, 0.5000, 0.0100, '华东供应链', 50.0000, 'on', '热销商品', '2026-04-20 10:00:00', '2026-05-03 10:00:00'),
    (7001, 'SKU010002', '无线降噪耳机 v2', '3C数码', '华为', '件', '规格-M / 白', '691000000002', 159.00, 259.00, 0.4000, 0.0100, '深圳精工电子', 70.0000, 'on', '', '2026-04-21 10:00:00', '2026-05-03 10:00:00'),
    (7002, 'SKU010003', '极简办公椅 v1', '家居日用', '网易严选', '件', '规格-L / 灰', '691000000003', 329.00, 499.00, 8.5000, 0.1200, '杭州源头工厂', 20.0000, 'on', '', '2026-04-22 10:00:00', '2026-05-03 10:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO inbound.inbound_order (id, order_no, supplier_name, order_type, status, total_amount, remark, created_at)
VALUES
    (8000, 'RK20260503001', '华东供应链', 'purchase', 'draft', 5600.00, '加急处理', '2026-05-03 10:00:00'),
    (8001, 'RK20260502001', '深圳精工电子', 'purchase', 'approved', 3180.00, '', '2026-05-02 14:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO inbound.inbound_order_item (id, order_id, sku_code, product_name, quantity, unit_price, amount)
VALUES
    (8100, 8000, 'SKU010001', '智能手表 Pro v1', 10.0000, 199.00, 1990.00),
    (8101, 8000, 'SKU010002', '无线降噪耳机 v2', 20.0000, 180.50, 3610.00),
    (8102, 8001, 'SKU010003', '极简办公椅 v1', 6.0000, 530.00, 3180.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO outbound.outbound_order (id, order_no, customer_name, order_type, status, total_amount, remark, created_at)
VALUES
    (9000, 'CK20260503001', '京东物流', 'sale', 'draft', 8900.00, '', '2026-05-03 10:30:00'),
    (9001, 'CK20260502001', '天猫旗舰', 'sale', 'approved', 2990.00, '优先配送', '2026-05-02 11:20:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO outbound.outbound_order_item (id, order_id, sku_code, product_name, quantity, unit_price, amount)
VALUES
    (9100, 9000, 'SKU010001', '智能手表 Pro v1', 20.0000, 299.00, 5980.00),
    (9101, 9000, 'SKU010002', '无线降噪耳机 v2', 10.0000, 292.00, 2920.00),
    (9102, 9001, 'SKU010003', '极简办公椅 v1', 5.0000, 598.00, 2990.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO inventory.inventory (id, product_id, sku_code, product_name, category_name, warehouse_name, available_qty, locked_qty, total_qty, safety_stock, version, updated_at)
VALUES
    (10000, 7000, 'SKU010001', '智能手表 Pro v1', '3C数码', '上海中心仓', 120.0000, 0.0000, 120.0000, 50.0000, 0, '2026-05-03 10:00:00'),
    (10001, 7001, 'SKU010002', '无线降噪耳机 v2', '3C数码', '广州前置仓', 20.0000, 0.0000, 20.0000, 70.0000, 0, '2026-05-03 10:00:00'),
    (10002, 7002, 'SKU010003', '极简办公椅 v1', '家居日用', '上海中心仓', 8.0000, 0.0000, 8.0000, 20.0000, 0, '2026-05-03 10:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO inventory.inventory_check_order (id, check_no, warehouse_name, status, check_date, created_at)
VALUES
    (10100, 'PD20260503001', '上海中心仓', 'draft', '2026-05-03', '2026-05-03 12:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO inventory.inventory_check_item (id, check_order_id, sku_code, product_name, system_qty, actual_qty, diff_qty)
VALUES
    (10110, 10100, 'SKU010001', '智能手表 Pro v1', 120.0000, 118.0000, -2.0000),
    (10111, 10100, 'SKU010002', '无线降噪耳机 v2', 20.0000, 20.0000, 0.0000)
ON CONFLICT (id) DO NOTHING;

INSERT INTO integration.sync_task (id, task_name, system_name, api_url, auth_type, sync_type, trigger_type, cron_expr, status, last_sync_time, created_at, updated_at)
VALUES
    (11000, 'ERP 商品主数据同步', '金蝶 ERP', 'https://erp.example.com/api/sku', 'bearer', 'incremental', 'schedule', '0 */30 * * * ?', 'success', '2026-05-03 09:30:00', '2026-05-01 10:00:00', '2026-05-03 09:30:00'),
    (11001, 'WMS 库存上报', '集团数据中台', 'https://dc.example.com/inventory', 'bearer', 'full', 'manual', NULL, 'failed', '2026-05-02 20:00:00', '2026-05-01 11:00:00', '2026-05-02 20:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO integration.sync_field_mapping (id, task_id, source_field, target_field, convert_rule)
VALUES
    (11100, 11000, 'sku_id', 'skuCode', NULL),
    (11101, 11000, 'sku_name', 'skuName', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO integration.sync_log (id, task_id, task_name, start_time, end_time, sync_count, success_count, fail_count, status, error_message)
VALUES
    (11200, 11000, 'ERP 商品主数据同步', '2026-05-03 09:30:00', '2026-05-03 09:31:00', 1280, 1280, 0, 'success', NULL),
    (11201, 11001, 'WMS 库存上报', '2026-05-02 20:00:00', '2026-05-02 20:00:30', 360, 0, 360, 'failed', '连接超时：30s 内未收到响应')
ON CONFLICT (id) DO NOTHING;

INSERT INTO report.report_task (id, report_type, query_params, generate_status, file_path, created_by, created_at)
VALUES
    (12000, 'inbound-summary', '{"startDate":"2026-01-01","endDate":"2026-05-03"}', 'success', '/downloads/inbound-summary.xlsx', 1000, '2026-05-03 13:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO report.ai_analysis_record (id, analysis_type, input_params, result_text, result_json, created_at)
VALUES
    (12100, 'inventory-forecast', '{"prompt":"预测下月库存需求"}', '近期出库稳定增长，建议优先补货 3C 数码与家居类热销商品。', '{"confidence":0.92}', '2026-05-03 13:30:00')
ON CONFLICT (id) DO NOTHING;
