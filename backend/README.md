# HOF WMS Backend

按 `backend-design.md` 搭建的 Java 17 + Spring Boot 3.x 多模块后端骨架，当前已经整理成“一个后端工程 + 公共模块 + 服务模块”的结构。

目录结构：

```text
backend/
├── build.gradle
├── settings.gradle
├── common/
│   ├── common-core/
│   ├── common-db/
│   └── common-web/
├── services/
│   ├── gateway-service/
│   ├── auth-service/
│   ├── master-data-service/
│   ├── inbound-service/
│   ├── outbound-service/
│   ├── inventory-service/
│   ├── integration-service/
│   ├── report-service/
│   └── ai-service/
└── sql/
```

说明：

- 这是一个 Gradle 多模块项目，不是多个独立仓库。
- 每个 `service` 模块保留自己的 `SpringBootApplication` 入口，这是微服务架构下的正常形式。
- `common` 下的模块提供公共常量、Web 响应封装、数据库配置等复用能力。

## 启动

数据库连接已按你提供的信息写入各服务配置：

- `jdbc:postgresql://localhost:5432/hof-wms`
- `username: insightowl`
- `password: insightowl`

前端开发环境端口已调整为 `5173`，API 代理到网关 `http://localhost:8080`。

## Docker

一键启动整套容器：

```bash
docker compose up --build
```

启动后访问：

- 前端: `http://localhost:5173`
- 网关: `http://localhost:8080`
- Redis: `localhost:6379`
- Elasticsearch: `http://localhost:9200`

说明：

- 当前 `docker-compose.yml` 不再启动 PostgreSQL，而是通过 `host.docker.internal:5432` 连接你已在运行的 `insightowl-postgres`
- 前端容器使用 `nginx` 托管静态文件，并把 `/api` 代理到 `gateway-service`
- 各 Spring 服务通过环境变量连接容器内的 `postgres`、`redis`、`elasticsearch`

如果需要初始化数据库，请对现有 `insightowl-postgres` 执行：

```bash
docker exec -i insightowl-postgres psql -U insightowl -d hof-wms < backend/sql/01_schema.sql
docker exec -i insightowl-postgres psql -U insightowl -d hof-wms < backend/sql/02_mock_data.sql
```

## 初始化数据库

先执行：

```bash
psql -h localhost -U insightowl -d hof-wms -f backend/sql/01_schema.sql
psql -h localhost -U insightowl -d hof-wms -f backend/sql/02_mock_data.sql
```

## 已实现模块

- `auth` 登录
- `dashboard` 仪表盘汇总
- `sku` 商品与分类
- `inbound` 入库单
- `outbound` 出库单
- `inventory` 库存、盘点、预警
- `data-sync` 同步任务与日志
- `system` 用户、角色、权限
- `report` 报表详情、导出占位、AI 分析

## 说明

- SQL 已覆盖 `auth / master / inbound / outbound / inventory / integration / report` 这几个 schema。
- 当前 Controller 先返回 mock 结构，便于前端继续联调；下一步可以继续替换成 MyBatis-Plus 实体、Mapper、Service 和真实查询。
