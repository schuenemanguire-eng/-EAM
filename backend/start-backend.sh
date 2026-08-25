#!/usr/bin/env bash
# ============================================================
# EAM 后端启动包装脚本
# 用于 CloudStudio（及本地）启动 Java 后端服务
# 作用：等待 sidecar MySQL 就绪 → 执行 schema.sql 建表初始化 → 启动 Spring Boot
# 说明：
#   - 本地运行（无 sidecar MySQL）时，脚本自动跳过建表，直接启动后端
#   - CloudStudio 中 sidecar MySQL 在 preview 启动前已运行，脚本负责等待并初始化
#   - MySQL 连接信息（密码、库名）需与 workspace.yml 的 sidecar.mysql 保持一致
#   - 通过 spring profile cloudstudio 切换为 CloudStudio 数据库配置（密码 123456）
# ============================================================

MYSQL_HOST="127.0.0.1"
MYSQL_PORT="3306"
MYSQL_USER="root"
MYSQL_PASSWORD="123456"
MYSQL_DATABASE="eam_db"
SCHEMA_SQL="$(dirname "$0")/src/main/resources/sql/schema.sql"
MAX_RETRY=60

echo "========== EAM 后端启动 =========="
echo "等待 MySQL 就绪（${MYSQL_HOST}:${MYSQL_PORT}）..."

i=0
while [ $i -lt $MAX_RETRY ]; do
  if (exec 3<>/dev/tcp/"${MYSQL_HOST}"/"${MYSQL_PORT}") 2>/dev/null; then
    exec 3<&- 2>/dev/null || true
    exec 3>&- 2>/dev/null || true
    echo "MySQL 已就绪"
    break
  fi
  i=$((i + 1))
  echo "  等待中... (${i}/${MAX_RETRY})"
  sleep 2
done

if [ $i -ge $MAX_RETRY ]; then
  echo "错误：${MAX_RETRY} 次尝试后 MySQL 仍未就绪，将直接启动后端（可能因数据库未建表而失败）"
fi

# 初始化数据库（仅当 MySQL 可用时执行）
if mysql -h"${MYSQL_HOST}" -P"${MYSQL_PORT}" -u"${MYSQL_USER}" -p"${MYSQL_PASSWORD}" -e "SELECT 1" &>/dev/null; then
  echo "执行数据库初始化脚本: ${SCHEMA_SQL}"
  mysql -h"${MYSQL_HOST}" -P"${MYSQL_PORT}" -u"${MYSQL_USER}" -p"${MYSQL_PASSWORD}" <"${SCHEMA_SQL}"
  if [ $? -eq 0 ]; then
    echo "数据库初始化完成（${MYSQL_DATABASE}）"
  else
    echo "警告：schema.sql 执行失败，如库已存在且表已建好可忽略"
  fi
else
  echo "MySQL 不可用（本地环境），跳过建表初始化"
fi

echo "启动 Spring Boot 后端..."
cd "$(dirname "$0")" || exit 1
# cloudstudio 为 Spring profile，用于切换 CloudStudio sidecar MySQL 配置（密码 123456）
# 本地若也走此脚本，仍会自动选择 cloudstudio profile
exec mvn spring-boot:run -f pom.xml -Dspring-boot.run.profiles=cloudstudio
