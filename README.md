# 企业员工管理系统 (Enterprise Employee Management System)

## 技术栈

### 后端
- Java 17 + Spring Boot 2.7.18
- MySQL 8.0 + MyBatis-Plus 3.5.5
- Sa-Token 1.37.0（权限认证）
- Knife4j（API文档）
- WebSocket（审批/打卡通知）
- JUC @EnableAsync（异步任务）

### 前端
- Vue 3 + Vite 6
- Ant Design Vue 4.x
- Axios + Pinia + TypeScript

## 本地部署

### 1. 数据库初始化
```bash
mysql -u root -p < backend/src/main/resources/sql/schema.sql
```

### 2. 后端启动
修改 `backend/src/main/resources/application.yml` 中的数据库密码，然后：
```bash
cd backend
# 使用指定的 Maven
D:\CodeSources\apache-maven-3.9.4\bin\mvn.cmd clean package -DskipTests
java -jar target/enterprise-employee-management-1.0.0.jar
```
后端启动端口: `http://localhost:8080`

### 3. 前端启动
```bash
cd frontend
npm install
npm run dev
```
前端启动端口: `http://localhost:3000`

### 4. 测试账号
| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 系统管理员 |
| li_jl | 123456 | 部门主管（研发部） |
| wang_zg | 123456 | 人事 |
| liu_gc | 123456 | 普通员工 |

## API 文档
后端启动后访问: `http://localhost:8080/doc.html`

## 项目结构

```
company_manage/
├── backend/          # Spring Boot 后端（DDD分层）
│   ├── src/main/java/com/company/eam/
│   │   ├── api/              # 接口层（Controller + DTO）
│   │   ├── application/      # 应用服务层（业务编排）
│   │   ├── domain/           # 领域层（实体 + 仓储接口 + 领域服务）
│   │   ├── infrastructure/   # 基础设施层（PO + Mapper + Repository实现 + 配置）
│   │   └── common/           # 通用组件
│   └── src/main/resources/
│       ├── application.yml
│       └── sql/schema.sql
└── frontend/         # Vue 3 前端
    └── src/
        ├── api/        # API接口封装
        ├── router/     # 路由配置
        ├── stores/     # Pinia状态管理
        ├── views/      # 页面组件
        └── utils/      # 工具类
```

## 功能模块
1. **部门管理** - 树形部门结构，增删改查
2. **职位管理** - 职位增删改查，职级P1-P8
3. **员工管理** - 员工增删改查、离职处理
4. **考勤管理** - 上下班打卡、考勤记录查询
5. **请假管理** - 请假申请、单级审批（部门主管）
6. **薪资管理** - 薪资录入、按年月查询
7. **数据看板** - 员工/部门/考勤/请假统计
