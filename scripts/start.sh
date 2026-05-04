#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "[HOF-WMS] 检查外部 PostgreSQL 容器..."
if ! docker ps --format '{{.Names}}' | grep -qx 'insightowl-postgres'; then
  echo "[ERROR] 未发现正在运行的 PostgreSQL 容器 insightowl-postgres"
  echo "请先启动外部数据库容器后再执行本脚本。"
  exit 1
fi

echo "[HOF-WMS] 启动应用容器..."
docker compose up -d --build

echo "[HOF-WMS] 启动完成"
echo "前端地址: http://localhost:5173"
echo "网关地址: http://localhost:8080"
