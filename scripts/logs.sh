#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

TARGET="${1:-all}"

if [[ "$TARGET" == "all" ]]; then
  docker compose logs -f
else
  docker compose logs -f "$TARGET"
fi
