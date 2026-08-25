#!/usr/bin/env bash
# ==========================================================
# CloudStudio 全栈沙箱 — 一键启动脚本
# 自动安装依赖并同时启动前后端服务
# ==========================================================

set -e
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
echo "项目根目录: ${ROOT_DIR}"

# ---------- 1. 安装依赖 ----------
echo "========== [1/3] 安装前端依赖 =========="
if [ ! -d "${ROOT_DIR}/frontend/node_modules" ]; then
  (cd "${ROOT_DIR}/frontend" && npm install)
else
  echo "frontend/node_modules 已存在，跳过 npm install"
fi

# 后端 Maven 依赖由 Maven 首次运行自动下载，无需单独安装

# ---------- 2. 启动后端 ----------
echo "========== [2/3] 启动后端（后台运行） =========="
# start-backend.sh 会等待 MySQL 就绪 → 执行 schema.sql 建表 → 启动 Spring Boot
(cd "${ROOT_DIR}/backend" && bash start-backend.sh > /tmp/eam-backend.log 2>&1 &)
echo "后端日志: /tmp/eam-backend.log"

# ---------- 3. 启动前端 ----------
echo "========== [3/3] 启动前端（前台运行） =========="
cd "${ROOT_DIR}/frontend" && npm run dev
