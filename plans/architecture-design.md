# 架构设计文档

## 1. 文档概述

### 1.1 项目名称
HOF-WMS 仓储管理系统

### 1.2 项目目标
构建一套涵盖进货、销售、库存管理的仓储管理系统，支持外部系统数据对接、报表生成与 AI 智能分析，满足企业仓储业务数字化管理需求。

### 1.3 设计原则

| 原则 | 说明 |
|------|------|
| 高内聚低耦合 | 各服务职责单一，通过接口通信 |
| 可扩展性 | 模块化设计，新业务可快速接入 |
| 可观测性 | 统一日志、链路追踪、指标监控 |
| 安全性 | 认证鉴权、数据加密、审计日志 |
| 容器化部署 | Docker 容器化，环境一致性 |

---

## 2. 系统总体架构

### 2.1 架构分层图

```mermaid
flowchart TB
    subgraph Client[客户端层]
        Browser[浏览器 - Vue3 SPA]
    end

    subgraph Gateway[网关层]
        GW[Spring Cloud Gateway]
    end

    subgraph BizServices[业务服务层]
        AUTH[auth-service]
        MASTER[master-data-service]
        INBOUND[inbound-service]
        OUTBOUND[outbound-service]
        INVENTORY[inventory-service]
        INTEGRATION[integration-service]
        REPORT[report-service]
        AI[ai-service]
    end

    subgraph DataLayer[数据层]
        PG[(PostgreSQL)]
        RD[(Redis)]
        ES[(Elasticsearch)]
    end

    subgraph External[外部系统]
        EXT1[外部ERP]
        EXT2[外部供应链]
        AIMODEL[AI大模型API]
    end

    Browser --> GW
    GW --> AUTH
    GW --> MASTER
    GW --> INBOUND
    GW --> OUTBOUND
    GW --> INVENTORY
    GW --> INTEGRATION
    GW --> REPORT
    GW --> AI

    AUTH --> PG
    AUTH --> RD
    MASTER --> PG
    MASTER --> RD
    INBOUND --> PG
    OUTBOUND --> PG
    INVENTORY --> PG
    INVENTORY --> RD
    INTEGRATION --> PG
    INTEGRATION --> EXT1
    INTEGRATION --> EXT2
    REPORT --> PG
    REPORT --> ES
    AI --> AIMODEL
```

### 2.2 技术架构分层

| 层次 | 技术选型 | 职责 |
|------|----------|------|
| 展示层 | Vue3 + Element Plus | 用户界面交互 |
| 网关层 | Spring Cloud Gateway | 路由、鉴权、限流、日志 |
| 业务层 | Spring Boot 3.x | 业务逻辑处理 |
| 数据访问层 | MyBatis-Plus | ORM 映射、数据操作 |
| 缓存层 | Redis | 热点数据缓存、分布式锁、会话管理 |
| 搜索层 | Elasticsearch | 全文检索、报表数据加速查询 |
| 持久层 | PostgreSQL | 业务数据持久化 |
| 部署层 | Docker + docker-compose | 容器化部署与编排 |

---

## 3. 微服务架构设计

### 3.1 服务清单

```mermaid
flowchart LR
    subgraph 公共模块
        CC[common-core]
        CD[common-db]
        CW[common-web]
    end

    subgraph 基础服务
        GW2[gateway-service :8080]
        AU[auth-service :8081]
    end

    subgraph 业务服务
        MD[master-data-service :8082]
        IB[inbound-service :8083]
        OB[outbound-service :8084]
        IV[inventory-service :8085]
    end

    subgraph 扩展服务
        IG[integration-service :8086]
        RP[report-service :8087]
        AS[ai-service :8088]
    end

    CC --> CD
    CC --> CW
    CD --> MD
    CD --> IB
    CD --> OB
    CD --> IV
    CD --> IG
    CD --> RP
```

### 3.2 服务间通信

| 通信方式 | 场景 | 说明 |
|----------|------|------|
| HTTP REST | 同步调用 | 服务间通过 OpenFeign 调用 |
| 事件驱动 | 异步通知 | 入库/出库完成后通知库存服务更新 |
| 定时任务 | 调度触发 | 数据同步定时任务 |

### 3.3 服务间调用关系

```mermaid
flowchart TD
    INBOUND2[inbound-service] -->|确认入库时调用| INVENTORY2[inventory-service]
    OUTBOUND2[outbound-service] -->|确认出库时调用| INVENTORY2
    OUTBOUND2 -->|校验库存| INVENTORY2
    REPORT2[report-service] -->|查询业务数据| INBOUND2
    REPORT2 -->|查询业务数据| OUTBOUND2
    REPORT2 -->|查询库存数据| INVENTORY2
    AI2[ai-service] -->|获取分析数据| REPORT2
    INTEGRATION2[integration-service] -->|写入主数据| MASTER2[master-data-service]
    INTEGRATION2 -->|写入入库数据| INBOUND2
```

---

## 4. 数据架构设计

### 4.1 数据库设计原则

- 每个微服务独立数据库 Schema，避免跨服务直接访问数据库
- 使用逻辑删除，不做物理删除
- 所有表包含 created_at, updated_at, created_by, updated_by 审计字段
- 主键使用雪花算法生成的 bigint
- 金额字段使用 numeric(18,2)，数量字段使用 numeric(18,4)

### 4.2 数据库 Schema 划分

| Schema | 服务 | 核心表 |
|--------|------|--------|
| auth | auth-service | sys_user, sys_role, sys_permission, sys_user_role, sys_role_permission |
| master | master-data-service | product, warehouse, supplier, customer |
| inbound | inbound-service | inbound_order, inbound_order_item, inbound_record |
| outbound | outbound-service | outbound_order, outbound_order_item, outbound_record |
| inventory | inventory-service | inventory, inventory_transaction, inventory_check_order, inventory_check_item |
| integration | integration-service | sync_task, sync_field_mapping, sync_log |
| report | report-service | report_task, ai_analysis_record |

### 4.3 数据流向图

```mermaid
flowchart LR
    EXT[外部系统] -->|数据同步| INTDB[(integration schema)]
    INTDB -->|写入| MASTERDB[(master schema)]
    INTDB -->|写入| INDB[(inbound schema)]

    INDB -->|入库确认| INVDB[(inventory schema)]
    OUTDB[(outbound schema)] -->|出库确认| INVDB

    INDB -->|统计| RPTDB[(report schema)]
    OUTDB -->|统计| RPTDB
    INVDB -->|统计| RPTDB
    RPTDB -->|索引| ESDB[(Elasticsearch)]
```

---

## 5. 安全架构设计

### 5.1 认证流程

```mermaid
sequenceDiagram
    participant U as 用户浏览器
    participant GW as API Gateway
    participant Auth as auth-service
    participant Biz as 业务服务

    U->>GW: POST /api/auth/login
    GW->>Auth: 转发登录请求
    Auth->>Auth: 校验用户名密码
    Auth-->>GW: 返回 JWT Token
    GW-->>U: 返回 Token

    U->>GW: GET /api/inbound/orders + Bearer Token
    GW->>GW: 校验 Token 有效性
    GW->>GW: 解析用户角色权限
    GW->>Biz: 转发请求 + 用户上下文
    Biz-->>GW: 返回业务数据
    GW-->>U: 返回数据
```

### 5.2 RBAC 权限模型

```mermaid
erDiagram
    SYS_USER ||--o{ SYS_USER_ROLE : has
    SYS_ROLE ||--o{ SYS_USER_ROLE : has
    SYS_ROLE ||--o{ SYS_ROLE_PERMISSION : has
    SYS_PERMISSION ||--o{ SYS_ROLE_PERMISSION : has
    SYS_ROLE ||--o{ SYS_ROLE_MENU : has
    SYS_MENU ||--o{ SYS_ROLE_MENU : has

    SYS_USER {
        bigint id PK
        varchar username
        varchar password_hash
        varchar real_name
        varchar phone
        varchar email
        varchar status
    }

    SYS_ROLE {
        bigint id PK
        varchar role_code
        varchar role_name
        varchar status
    }

    SYS_PERMISSION {
        bigint id PK
        varchar perm_code
        varchar perm_name
        varchar resource_type
    }

    SYS_MENU {
        bigint id PK
        bigint parent_id
        varchar menu_name
        varchar menu_path
        varchar icon
        integer sort_order
        varchar status
    }
```

### 5.3 预设角色

| 角色 | 权限范围 |
|------|----------|
| 超级管理员 | 全部权限 |
| 仓库管理员 | 入库、出库、库存全部操作 |
| 入库员 | 入库单创建、编辑、提交 |
| 出库员 | 出库单创建、编辑、提交 |
| 审核员 | 入库/出库单审核 |
| 数据管理员 | 数据对接模块全部操作 |
| 报表查看员 | 报表查看、导出 |

---

## 6. 缓存架构设计

### 6.1 缓存策略

| 缓存对象 | Key 格式 | 过期时间 | 更新策略 |
|----------|----------|----------|----------|
| 用户 Token | auth:token:{userId} | 2h | 登录刷新 |
| 用户权限 | auth:perm:{userId} | 30min | 权限变更时删除 |
| 商品信息 | master:product:{id} | 1h | 更新时删除 |
| 仓库信息 | master:warehouse:{id} | 1h | 更新时删除 |
| 字典数据 | master:dict:{type} | 2h | 更新时删除 |
| 库存锁 | inventory:lock:{warehouseId}:{productId} | 30s | 自动过期 |

### 6.2 缓存架构图

```mermaid
flowchart LR
    APP[业务服务] -->|1.查缓存| REDIS[(Redis)]
    REDIS -->|命中| APP
    REDIS -->|未命中| PG2[(PostgreSQL)]
    PG2 -->|回写缓存| REDIS
    PG2 -->|返回数据| APP
```

---

## 7. 部署架构设计

### 7.1 Docker 部署拓扑

```mermaid
flowchart TB
    subgraph DockerCompose[docker-compose]
        subgraph Infra[基础设施]
            PG3[(postgres:15)]
            RD3[(redis:7)]
            ES3[(elasticsearch:8)]
        end

        subgraph Services[应用服务]
            GW3[gateway-service]
            AU3[auth-service]
            MD3[master-data-service]
            IB3[inbound-service]
            OB3[outbound-service]
            IV3[inventory-service]
            IG3[integration-service]
            RP3[report-service]
            AI3[ai-service]
        end

        subgraph Frontend2[前端]
            NG[nginx + vue-spa]
        end
    end

    User[用户] --> NG
    NG -->|/api/*| GW3
    GW3 --> AU3
    GW3 --> MD3
    GW3 --> IB3
    GW3 --> OB3
    GW3 --> IV3
    GW3 --> IG3
    GW3 --> RP3
    GW3 --> AI3
```

### 7.2 端口规划

| 服务 | 端口 |
|------|------|
| Nginx - 前端 | 80 |
| gateway-service | 8080 |
| auth-service | 8081 |
| master-data-service | 8082 |
| inbound-service | 8083 |
| outbound-service | 8084 |
| inventory-service | 8085 |
| integration-service | 8086 |
| report-service | 8087 |
| ai-service | 8088 |
| PostgreSQL | 5432 |
| Redis | 6379 |
| Elasticsearch | 9200 |

---

## 8. 可观测性设计

### 8.1 日志规范

- 统一日志格式：JSON 结构化日志
- 必含字段：timestamp, traceId, spanId, service, level, message
- 日志级别：ERROR > WARN > INFO > DEBUG
- 生产环境默认 INFO 级别

### 8.2 监控指标

| 指标类型 | 指标项 |
|----------|--------|
| 业务指标 | 入库单数量、出库单数量、库存预警数 |
| 性能指标 | 接口响应时间 P99、QPS |
| 系统指标 | CPU、内存、磁盘使用率 |
| 同步指标 | 同步成功率、同步延迟 |
