#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "===== 外部 PostgreSQL 容器 ====="
docker ps --filter "name=insightowl-postgres" --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'

echo
echo "===== HOF-WMS 容器 ====="
docker compose ps
