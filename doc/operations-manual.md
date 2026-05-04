# HOF-WMS 运营手册

## 1. 文档目的

本文档用于指导 HOF-WMS 项目的日常启动、停止、初始化、巡检和问题排查，适用于开发、测试、运维和业务支持人员。

## 2. 系统概览

HOF-WMS 当前采用“一个后端工程，多模块微服务”的架构，前端通过网关统一访问后端接口。

### 2.1 访问入口

- 前端访问地址：`http://localhost:5173`
- 网关地址：`http://localhost:8080`
- Redis：`localhost:6379`
- Elasticsearch：`http://localhost:9200`
- PostgreSQL：复用外部容器 `insightowl-postgres`

### 2.2 后端模块说明

#### 公共模块

- `common-core`
  作用：公共常量、基础工具类、后续可扩展的统一枚举和领域基础能力。
- `common-db`
  作用：数据库、MyBatis-Plus、Redis 等通用配置能力。
- `common-web`
  作用：统一响应结构、全局异常处理、Web 层通用封装。

#### 服务模块

- `gateway-service`
  作用：系统统一入口，负责路由转发。
- `auth-service`
  作用：登录、用户、角色、权限相关接口。
- `master-data-service`
  作用：商品、商品分类等主数据接口。
- `inbound-service`
  作用：入库单管理、提交审核、审核、确认入库。
- `outbound-service`
  作用：出库单管理、提交审核、审核、确认出库。
- `inventory-service`
  作用：库存查询、库存预警、盘点、仪表盘汇总。
- `integration-service`
  作用：数据同步任务、同步日志、执行与测试连接。
- `report-service`
  作用：报表详情、导出、AI 分析入口。
- `ai-service`
  作用：AI 服务扩展模块，当前作为独立服务保留。

## 3. 当前功能模块介绍

### 3.1 登录与系统管理

- 用户登录
- 用户管理
- 角色管理
- 权限管理

### 3.2 商品中心

- SKU 列表
- SKU 详情
- 商品分类管理

### 3.3 入库管理

- 入库单列表
- 新建入库单
- 保存草稿
- 提交审核
- 审核通过 / 驳回
- 确认入库

### 3.4 出库管理

- 出库单列表
- 新建出库单
- 保存草稿
- 提交审核
- 审核通过 / 驳回
- 确认出库

### 3.5 库存管理

- 库存查询
- 库存预警
- 安全库存阈值设置
- 盘点单保存与提交

### 3.6 数据对接

- 同步任务列表
- 同步任务配置
- 测试连接
- 手动执行
- 同步日志

### 3.7 报表与分析

- 仪表盘
- 报表详情
- 导出入口
- AI 智能分析

## 4. 启动前准备

### 4.1 软件要求

- Docker
- Docker Compose

### 4.2 外部依赖要求

当前项目默认复用一个已经存在的 PostgreSQL 容器：

- 容器名：`insightowl-postgres`
- 数据库名：`hof-wms`
- 用户名：`insightowl`
- 密码：`insightowl`

### 4.3 目录说明

- 容器编排文件：[`docker-compose.yml`](/Users/nan.wang11/my_workspace/hof-wms/docker-compose.yml)
- 启动脚本目录：[`scripts`](/Users/nan.wang11/my_workspace/hof-wms/scripts)
- SQL 目录：[`backend/sql`](/Users/nan.wang11/my_workspace/hof-wms/backend/sql)

## 5. 常用脚本

### 5.1 启动系统

```bash
./scripts/start.sh
```

功能：

- 检查 `insightowl-postgres` 是否存在
- 启动 Redis、Elasticsearch、后端服务、前端服务

### 5.2 停止系统

```bash
./scripts/stop.sh
```

功能：

- 停止当前项目容器
- 保留数据卷

### 5.3 查看运行状态

```bash
./scripts/status.sh
```

功能：

- 查看本项目容器状态
- 查看外部 PostgreSQL 容器状态

### 5.4 查看日志

```bash
./scripts/logs.sh
```

查看全部日志：

```bash
./scripts/logs.sh all
```

查看指定服务日志：

```bash
./scripts/logs.sh gateway-service
./scripts/logs.sh frontend
```

### 5.5 初始化数据库

```bash
./scripts/init-db.sh
```

功能：

- 对 `insightowl-postgres` 执行建表脚本
- 导入 mock 数据

## 6. 标准操作流程

### 6.1 首次部署流程

1. 确认外部 PostgreSQL 容器 `insightowl-postgres` 已运行。
2. 执行数据库初始化脚本。
3. 启动整套应用。
4. 检查服务状态。
5. 访问前端页面验证登录与基础功能。

### 6.2 日常重启流程

1. 执行停止脚本。
2. 执行启动脚本。
3. 检查网关和前端是否可访问。

### 6.3 发布后验证建议

- 登录功能是否正常
- 仪表盘是否能正常加载
- SKU 列表是否能打开
- 入库、出库、库存页面是否能返回数据
- 数据同步、报表、AI 分析页面是否能正常响应

## 7. 运行检查项

### 7.1 容器检查

重点确认以下容器状态：

- `hof-wms-redis`
- `hof-wms-elasticsearch`
- `hof-wms-gateway-service` 对应 compose 服务 `gateway-service`
- `hof-wms-frontend` 对应 compose 服务 `frontend`

### 7.2 接口检查

建议至少检查：

- `http://localhost:8080/api/auth/login`
- `http://localhost:8080/api/dashboard/summary`

### 7.3 页面检查

- `http://localhost:5173/login`
- `http://localhost:5173/dashboard`

## 8. 故障排查

### 8.1 前端打不开

排查方向：

- `frontend` 容器是否启动
- `5173` 端口是否被占用
- `nginx` 配置是否正确

### 8.2 网关无法访问

排查方向：

- `gateway-service` 是否启动成功
- 后端子服务是否都正常
- 查看网关日志中是否有路由转发失败

### 8.3 后端报数据库连接失败

排查方向：

- `insightowl-postgres` 是否运行
- 是否能够从 Docker 容器访问 `host.docker.internal:5432`
- 数据库用户名密码是否仍为当前配置

### 8.4 Redis 或 Elasticsearch 不可用

排查方向：

- 对应容器是否健康
- 本机端口 `6379`、`9200` 是否冲突

### 8.5 数据库初始化失败

排查方向：

- `insightowl-postgres` 是否存在
- 容器内是否有 `psql`
- SQL 是否被重复执行导致约束冲突

## 9. 配置说明

### 9.1 数据库配置

默认通过容器环境变量注入：

- `DB_HOST=host.docker.internal`
- `DB_PORT=5432`
- `DB_NAME=hof-wms`
- `DB_USER=insightowl`
- `DB_PASSWORD=insightowl`

### 9.2 网关配置

网关通过环境变量路由到各服务：

- `AUTH_SERVICE_URL`
- `MASTER_DATA_SERVICE_URL`
- `INBOUND_SERVICE_URL`
- `OUTBOUND_SERVICE_URL`
- `INVENTORY_SERVICE_URL`
- `INTEGRATION_SERVICE_URL`
- `REPORT_SERVICE_URL`
- `AI_SERVICE_URL`

## 10. 建议的后续运营完善项

- 增加统一健康检查接口
- 增加 Prometheus / Grafana 监控
- 增加日志采集与集中检索
- 将数据库、Redis、ES 的账号密码改为 `.env` 管理
- 将 mock Controller 逐步替换为真实 MyBatis-Plus 持久化实现
