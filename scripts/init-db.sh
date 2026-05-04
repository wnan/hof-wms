#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

CONTAINER_NAME="insightowl-postgres"

echo "[HOF-WMS] 检查 PostgreSQL 容器..."
if ! docker ps --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  echo "[ERROR] 未发现正在运行的 PostgreSQL 容器 $CONTAINER_NAME"
  exit 1
fi

echo "[HOF-WMS] 执行建表脚本..."
docker exec -i "$CONTAINER_NAME" psql -U insightowl -d hof-wms < backend/sql/01_schema.sql

echo "[HOF-WMS] 导入 mock 数据..."
docker exec -i "$CONTAINER_NAME" psql -U insightowl -d hof-wms < backend/sql/02_mock_data.sql

echo "[HOF-WMS] 数据库初始化完成"
