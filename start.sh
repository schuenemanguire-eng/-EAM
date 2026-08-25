#!/usr/bin/env bash
# ==========================================================
# CloudStudio 全栈沙箱 — 一键启动脚本
# 自动安装/启动 MySQL → 初始化数据库 → 启动前后端服务
# 一次 sh start.sh 全搞定
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
SCHEMA_SQL="$BACKEND_DIR/src/main/resources/sql/schema.sql"

MYSQL_HOST="127.0.0.1"
MYSQL_PORT="3306"
MYSQL_USER="root"
MYSQL_PASSWORD="123456"
MYSQL_DATABASE="eam_db"

# --------------- MySQL 自动拉起 + 建表 ---------------
ensure_mysql() {
  echo -e "${YELLOW}[1/5] 检查 MySQL 环境...${NC}"

  # 1) 安装（如缺失）
  if ! command -v mysql >/dev/null 2>&1; then
    echo "  未检测到 MySQL，开始安装（首次约需几分钟）..."
    sudo apt-get update -qq
    sudo DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server mysql-client
  fi

  # 2) 启动（如未运行）
  if ! sudo mysql -e "SELECT 1" &>/dev/null; then
    echo "  启动 MySQL 服务..."
    sudo service mysql start || sudo /etc/init.d/mysql start
  fi

  # 3) 等待 socket 就绪
  echo "  等待 MySQL 就绪..."
  for i in $(seq 1 30); do
    if sudo mysql -e "SELECT 1" &>/dev/null; then
      echo "  ✅ MySQL 服务已就绪"
      break
    fi
    sleep 2
  done

  # 4) 确保 root 密码为 123456（幂等，MySQL8.4 兼容两种写法）
  sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '${MYSQL_PASSWORD}'; FLUSH PRIVILEGES;" 2>/dev/null \
    || sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_PASSWORD}'; FLUSH PRIVILEGES;" 2>/dev/null \
    || true

  # 5) 等待密码可登录（保证后端 JDBC 能连上）
  for i in $(seq 1 15); do
    if mysql -h"${MYSQL_HOST}" -uroot -p"${MYSQL_PASSWORD}" -e "SELECT 1" &>/dev/null; then
      echo "  ✅ MySQL 可登录（${MYSQL_USER}/${MYSQL_PASSWORD}）"
      break
    fi
    sleep 2
  done

  # 6) 幂等建表（eam_db 不存在才执行 schema.sql）
  DB_EXISTS=$(mysql -h"${MYSQL_HOST}" -uroot -p"${MYSQL_PASSWORD}" -N -e "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME='${MYSQL_DATABASE}';" 2>/dev/null)
  if [ -z "$DB_EXISTS" ]; then
    echo -e "${YELLOW}[2/5] 初始化数据库（schema.sql）...${NC}"
    mysql -h"${MYSQL_HOST}" -uroot -p"${MYSQL_PASSWORD}" <"${SCHEMA_SQL}"
    echo -e "${GREEN}  ✅ 数据库初始化完成（${MYSQL_DATABASE}）${NC}"
  else
    echo -e "${YELLOW}[2/5] 数据库 ${MYSQL_DATABASE} 已存在，跳过建表（如需重置请手动执行 schema.sql）${NC}"
  fi
}

ensure_mysql

# --------------- 后端 ---------------
echo -e "${YELLOW}[3/5] 启动后端服务（Spring Boot, 端口 8080）...${NC}"
cd "$BACKEND_DIR"
bash start-backend.sh > /tmp/eam-backend.log 2>&1 &
BACKEND_PID=$!
echo -e "${GREEN}  后端 PID: $BACKEND_PID （日志: /tmp/eam-backend.log）${NC}"

# --------------- 前端依赖 ---------------
echo -e "${YELLOW}[4/5] 安装前端依赖...${NC}"
cd "$FRONTEND_DIR"
if [ ! -d "node_modules" ]; then
  npm install
fi

# --------------- 前端 ---------------
echo -e "${YELLOW}[5/5] 启动前端开发服务器 (端口 3000)...${NC}"
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
