#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "[HOF-WMS] 停止应用容器..."
docker compose down

echo "[HOF-WMS] 停止完成"
