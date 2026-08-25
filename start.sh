#!/usr/bin/env bash
# ==========================================================
# CloudStudio 全栈沙箱 — 一键启动脚本
# 自动安装依赖并同时启动前后端服务
# ==========================================================
set -e

# 颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

APP_NAME="📋 企业员工管理系统"

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  ${APP_NAME} — CloudStudio 全栈启动 ${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"

# --------------- 后端 ---------------
echo -e "${YELLOW}[1/4] 启动后端服务（等待 MySQL → 建表 → Spring Boot, 端口 8080）...${NC}"
cd "$BACKEND_DIR"
bash start-backend.sh > /tmp/eam-backend.log 2>&1 &
BACKEND_PID=$!
echo -e "${GREEN}  后端 PID: $BACKEND_PID （日志: /tmp/eam-backend.log）${NC}"

# --------------- 前端依赖 ---------------
echo -e "${YELLOW}[2/4] 安装前端依赖...${NC}"
cd "$FRONTEND_DIR"
if [ ! -d "node_modules" ]; then
  npm install
fi

# --------------- 前端 ---------------
echo -e "${YELLOW}[3/4] 启动前端开发服务器 (端口 3000)...${NC}"
cd "$FRONTEND_DIR"
npm run dev > /tmp/eam-frontend.log 2>&1 &
FRONTEND_PID=$!
echo -e "${GREEN}  前端 PID: $FRONTEND_PID （日志: /tmp/eam-frontend.log）${NC}"

# --------------- 完成 ---------------
echo ""
echo -e "${CYAN}========================================${NC}"
echo -e "${GREEN}  ✅ ${APP_NAME} 已启动！${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""
echo -e "  📡 前端地址:  ${GREEN}http://localhost:3000${NC}"
echo -e "  📡 后端地址:  ${GREEN}http://localhost:8080${NC}"
echo ""
echo -e "  ${YELLOW}💡 CloudStudio 会自动检测打开的端口，${NC}"
echo -e "  ${YELLOW}   生成可公开访问的预览链接。${NC}"
echo ""
echo -e "  ${YELLOW}按 Ctrl+C 停止所有服务${NC}"
echo ""

# 捕获退出信号，清理子进程
cleanup() {
  echo ""
  echo -e "${YELLOW}正在停止服务...${NC}"
  kill $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
  wait $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
  echo -e "${GREEN}服务已停止。${NC}"
  exit 0
}
trap cleanup SIGINT SIGTERM

# 保持前台运行
wait
