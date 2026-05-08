CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS master;
CREATE SCHEMA IF NOT EXISTS inbound;
CREATE SCHEMA IF NOT EXISTS outbound;
CREATE SCHEMA IF NOT EXISTS inventory;
CREATE SCHEMA IF NOT EXISTS integration;
CREATE SCHEMA IF NOT EXISTS report;

CREATE TABLE IF NOT EXISTS auth.sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    real_name VARCHAR(64) NOT NULL,
    email VARCHAR(128),
    phone VARCHAR(32),
    department VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS auth.sys_role (
    id BIGINT PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS auth.sys_permission (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(64) NOT NULL,
    code VARCHAR(64) NOT NULL UNIQUE,
    type VARCHAR(32) NOT NULL,
    path VARCHAR(128),
    sort INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS auth.sys_user_role (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS auth.sys_role_permission (
    id BIGINT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS master.product (
    id BIGINT PRIMARY KEY,
    sku_code VARCHAR(64) NOT NULL UNIQUE,
    product_name VARCHAR(128) NOT NULL,
    category_name VARCHAR(64) NOT NULL,
    brand VARCHAR(64),
    unit VARCHAR(32),
    spec VARCHAR(128),
    barcode VARCHAR(64),
    cost_price NUMERIC(18,2),
    sale_price NUMERIC(18,2),
    weight NUMERIC(18,4),
    volume NUMERIC(18,4),
    supplier_name VARCHAR(128),
    safety_stock NUMERIC(18,4),
    status VARCHAR(32) NOT NULL,
    remark VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS master.product_category (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(64) NOT NULL,
    sort INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS inbound.inbound_order (
    id BIGINT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    supplier_name VARCHAR(128) NOT NULL,
    order_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_amount NUMERIC(18,2) NOT NULL,
    remark VARCHAR(500),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS inbound.inbound_order_item (
    id BIGINT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    quantity NUMERIC(18,4) NOT NULL,
    unit_price NUMERIC(18,2) NOT NULL,
    amount NUMERIC(18,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS outbound.outbound_order (
    id BIGINT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    customer_name VARCHAR(128) NOT NULL,
    order_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_amount NUMERIC(18,2) NOT NULL,
    remark VARCHAR(500),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS outbound.outbound_order_item (
    id BIGINT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    quantity NUMERIC(18,4) NOT NULL,
    unit_price NUMERIC(18,2) NOT NULL,
    amount NUMERIC(18,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory.inventory (
    id BIGINT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    category_name VARCHAR(64) NOT NULL,
    warehouse_name VARCHAR(128) NOT NULL,
    available_qty NUMERIC(18,4) NOT NULL,
    locked_qty NUMERIC(18,4) NOT NULL DEFAULT 0,
    total_qty NUMERIC(18,4) NOT NULL,
    safety_stock NUMERIC(18,4) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_inventory_warehouse_sku UNIQUE (warehouse_name, sku_code)
);

CREATE TABLE IF NOT EXISTS inventory.inventory_check_order (
    id BIGINT PRIMARY KEY,
    check_no VARCHAR(64) NOT NULL UNIQUE,
    warehouse_name VARCHAR(128) NOT NULL,
    status VARCHAR(32) NOT NULL,
    check_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory.inventory_check_item (
    id BIGINT PRIMARY KEY,
    check_order_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    system_qty NUMERIC(18,4) NOT NULL,
    actual_qty NUMERIC(18,4) NOT NULL,
    diff_qty NUMERIC(18,4) NOT NULL
);

CREATE TABLE IF NOT EXISTS integration.sync_task (
    id BIGINT PRIMARY KEY,
    task_name VARCHAR(128) NOT NULL,
    system_name VARCHAR(64) NOT NULL,
    api_url VARCHAR(500) NOT NULL,
    auth_type VARCHAR(32) NOT NULL,
    sync_type VARCHAR(32) NOT NULL,
    trigger_type VARCHAR(32) NOT NULL,
    cron_expr VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    last_sync_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS integration.sync_field_mapping (
    id BIGINT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    source_field VARCHAR(128) NOT NULL,
    target_field VARCHAR(128) NOT NULL,
    convert_rule VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS integration.sync_log (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    task_name VARCHAR(128) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    sync_count INT NOT NULL DEFAULT 0,
    success_count INT NOT NULL DEFAULT 0,
    fail_count INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL,
    error_message VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS report.report_task (
    id BIGINT PRIMARY KEY,
    report_type VARCHAR(64) NOT NULL,
    query_params JSONB,
    generate_status VARCHAR(32) NOT NULL,
    file_path VARCHAR(255),
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS report.ai_analysis_record (
    id BIGINT PRIMARY KEY,
    analysis_type VARCHAR(64) NOT NULL,
    input_params JSONB,
    result_text TEXT,
    result_json JSONB,
    created_at TIMESTAMP NOT NULL
);
