# 功能规格说明书（Spec）

> 企业员工管理系统（EAM）· 版本 1.0.0
>
> 本文档从架构设计、数据库设计、API 接口设计、代码审查四个方面对系统进行完整规格描述。

---

## 1. 架构设计

### 1.1 总体架构

系统采用 **前后端分离 + 简化 DDD 分层** 的架构：

```
┌─────────────────────────── 浏览器 ───────────────────────────┐
│  Vue3 + Vite + Ant Design Vue + Pinia + TypeScript (SPA)     │
│  页面：登录/看板/部门/职位/员工/考勤/请假/薪资                 │
└──────────────┬────────────────────────────────────────────────┘
               │ HTTP (axios)  /api/*    代理 :3000 → :8080
┌──────────────▼────────────────────────────────────────────────┐
│ Spring Boot 后端 (8080)                                        │
│  api.controller  ──►  application.service  ──►  domain.service │
│        │                    │                    │             │
│        │                    │              domain.entity       │
│        │                    │              domain.repository(接口)│
│        └──────────┬─────────┴──────────────┬─────────┘          │
│               infrastructure (实现)                             │
│          persistence.po / mapper / repositoryImpl               │
│          config (Sa-Token / WebSocket / Async)                  │
└──────────────┬────────────────────────────────────────────────┘
               │ JDBC (MyBatis-Plus)
        ┌──────▼──────┐
        │ MySQL 8.0   │  eam_db  (utf8mb4 / InnoDB / 无外键)
        └─────────────┘
```

### 1.2 分层职责

| 层 | 包路径 | 职责 | 依赖 |
|----|--------|------|------|
| 接口层 | `com.company.eam.api` | REST 控制器、请求/响应 DTO | application |
| 应用层 | `com.company.eam.application` | 业务用例编排、事务、鉴权获取 | domain |
| 领域层 | `com.company.eam.domain` | 纯业务实体、领域服务、仓储接口、枚举 | 无 MyBatis/MySQL |
| 基础设施层 | `com.company.eam.infrastructure` | 数据库 PO、Mapper、仓储实现、框架配置 | domain |
| 通用层 | `com.company.eam.common` | 统一响应、全局异常、工具类 | - |

> DDD 约束：`domain.entity` 为纯业务实体，不使用 MyBatis 注解、不依赖数据库；`domain.repository` 定义仓储接口，实现在 `infrastructure.repository`，实现类内部将业务实体与 PO 相互转换。

### 1.3 核心组件

| 组件 | 说明 |
|------|------|
| Sa-Token | 登录认证（Token），除白名单外全部接口需登录，配置见 `SaTokenConfig` |
| WebSocket | 请假审批通知（`/ws/notifications`），内存级会话，`WebSocketHandler` |
| 异步任务 | `@EnableAsync` 线程池（`AsyncConfig`） |
| MyBatis-Plus | ORM，开启逻辑删除（字段 `deleted`）、下划线转驼峰 |
| Knife4j | OpenAPI3 接口文档，地址 `/doc.html` |
| 全局异常 | `GlobalExceptionHandler` 统一捕获 `BusinessException` 及系统异常 |

### 1.4 关键业务流程

**请假审批流程（单级审批）：**

```
员工提交申请
   │ 计算请假天数 leaveDomainService.calculateLeaveDays()
   │ 自动指定审批人 leaveDomainService.getApproverId()（取部门 manager_id）
   ▼
申请单保存，状态=待审批，approver_id=部门主管
   │ WebSocket 通知审批人
   ▼
主管查询待审批列表 → 批准/拒绝
   │ 校验当前登录人 == approver_id
   ▼
状态更新（已批准/已拒绝），WebSocket 通知申请人
```

**打卡流程：**

```
员工打卡（IN/OUT）
   │ 按 (employee_id, date) 查找当日记录
   ├─ 无记录 → 新增（记录上班/下班时间）
   └─ 有记录 → 更新（补记下班卡）
   │ 依据考勤规则阈值判定 状态（正常/迟到/早退/缺卡）
   ▼
保存并返回
```

### 1.5 前端结构

```
frontend/src
├── api/index.ts        # API 封装（axios）
├── stores/user.ts      # Pinia 用户状态
├── router/index.ts     # Vue Router（hash 模式）
├── utils/request.ts    # axios 实例（携带 Bearer Token）
└── views/
    ├── Login.vue       # 登录
    ├── layout/Layout.vue
    ├── dashboard/Dashboard.vue   # 数据看板
    ├── dept/Dept.vue             # 部门管理
    ├── position/Position.vue     # 职位管理
    ├── employee/Employee.vue     # 员工管理
    ├── attendance/Attendance.vue # 考勤管理
    ├── leave/Leave.vue           # 请假管理
    └── salary/Salary.vue         # 薪资管理
```

---

## 2. 数据库设计

### 2.1 设计约定

- 引擎 `InnoDB`，字符集 `utf8mb4`。
- **不建立外键**，关联校验全部在业务层实现。
- **逻辑删除**：业务表含 `deleted` 字段（0 未删 / 1 已删），禁止物理删除核心业务数据。
- 主键自增；`create_time`/`update_time` 自动维护。

### 2.2 表清单（9 张）

| # | 表名 | 说明 | 关键索引 |
|---|------|------|----------|
| 1 | `dept` | 部门表（含主管 manager_id） | 主键 |
| 2 | `position` | 职位表（职级 rank P1-P8） | 主键 |
| 3 | `employee` | 员工表 | `uk_employee_no` 工号唯一 |
| 4 | `sys_user` | 用户表（BCrypt 密码、角色） | `uk_username` 用户名唯一 |
| 5 | `attendance_rule` | 考勤规则表（仅 1 条） | 主键 |
| 6 | `attendance_record` | 打卡记录表 | `uk_emp_date` (employee_id,date) 唯一 |
| 7 | `leave_application` | 请假申请表 | `idx_emp_status` (employee_id,status) |
| 8 | `salary` | 薪资表 | `uk_emp_year_month` (employee_id,year,month) 唯一 |
| 9 | `attendance_monthly_stat` | 月度考勤统计表 | 主键 |

### 2.3 核心表结构

**dept（部门）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| name | VARCHAR(100) | 部门名称 |
| parent_id | BIGINT | 父部门 ID，0 表示根 |
| manager_id | BIGINT | 部门主管员工 ID |
| sort | INT | 排序号 |
| status | TINYINT | 1 启用 0 禁用 |
| deleted | TINYINT | 逻辑删除 |

**employee（员工）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| name | VARCHAR(50) | 姓名 |
| employee_no | VARCHAR(32) | 工号（唯一索引） |
| dept_id / position_id | BIGINT | 部门 / 职位 |
| entry_date | DATE | 入职日期 |
| status | TINYINT | 1 在职 0 离职 |
| phone / email | VARCHAR | 联系方式 |
| gender | TINYINT | 1 男 2 女 |
| birthday | DATE | 出生日期 |

**sys_user（用户）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| username | VARCHAR(50) | 登录名（唯一） |
| password | VARCHAR(200) | BCrypt 密文 |
| employee_id | BIGINT | 关联员工 |
| role | VARCHAR(20) | admin/hr/supervisor/employee |
| status | TINYINT | 1 启用 0 禁用 |

**attendance_record（打卡记录）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| employee_id | BIGINT | 员工 ID |
| clock_in_time / clock_out_time | DATETIME | 上班 / 下班打卡时间 |
| status | VARCHAR(20) | 正常/迟到/早退/缺卡 |
| date | DATE | 考勤日期（与 employee_id 联合唯一） |

**leave_application（请假申请）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| employee_id | BIGINT | 申请人 |
| leave_type | VARCHAR(20) | 年假/病假/事假/婚假 |
| start_time / end_time | DATETIME | 起止时间 |
| total_days | DECIMAL(4,2) | 请假天数（自动计算） |
| status | VARCHAR(20) | 待审批/已批准/已拒绝 |
| approver_id | BIGINT | 审批人（部门主管） |
| approve_time | DATETIME | 审批时间 |

**salary（薪资）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| employee_id | BIGINT | 员工 ID |
| year / month | INT | 年月（联合唯一） |
| base_salary / bonus / allowance / deduction | DECIMAL(10,2) | 基本工资/奖金/津贴/扣款 |
| total_salary | DECIMAL(10,2) | 实发工资（自动计算） |
| pay_date | DATE | 发放日期 |

### 2.4 初始化数据

- 4 个部门（总公司 + 研发部/人事部/市场部），`manager_id` 指向主管员工。
- 8 个职位（CEO、技术总监、人事主管、市场主管、高级/初级开发、人事专员、市场专员）。
- 10 名员工（EMP001~EMP010）与 10 个登录账号，密码均为 `123456`（BCrypt 密文）。
- 1 条考勤规则（09:00–18:00，迟到/早退阈值 15 分钟）。

---

## 3. API 接口设计

> 统一前缀 `/api`，除登录外均需请求头 `Authorization: Bearer <token>`。统一返回结构：

```json
{ "code": 200, "message": "success", "data": { } }
```

### 3.1 认证 Auth

| 方法 | 路径 | 说明 | 入参 |
|------|------|------|------|
| POST | `/api/auth/login` | 登录，返回 token + 用户信息 | username, password |
| GET | `/api/auth/currentUser` | 当前登录用户信息 | - |
| POST | `/api/auth/logout` | 退出登录 | - |
| PUT | `/api/auth/updateProfile` | 更新用户资料 | id, username, status |

### 3.2 部门 Dept

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/dept/tree` | 部门树（含子部门、主管名） |
| GET | `/api/dept/{id}` | 部门详情 |
| POST | `/api/dept` | 新增部门 |
| PUT | `/api/dept` | 修改部门 |
| DELETE | `/api/dept/{id}` | 删除部门 |

### 3.3 职位 Position

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/position` | 职位列表 |
| POST | `/api/position` | 新增职位 |
| PUT | `/api/position` | 修改职位 |
| DELETE | `/api/position/{id}` | 删除职位 |

### 3.4 员工 Employee

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/employee?keyword=&deptId=` | 员工列表（搜索/筛选） |
| GET | `/api/employee/{id}` | 员工详情 |
| POST | `/api/employee` | 新增员工（工号必填且唯一，空则自动生成） |
| PUT | `/api/employee` | 修改员工 |
| DELETE | `/api/employee/{id}` | 删除员工（逻辑删除） |
| PUT | `/api/employee/{id}/quit` | 办理离职 |

### 3.5 考勤 Attendance

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/attendance/clock` | 打卡（clockType: IN/OUT，date 默认当天） |
| GET | `/api/attendance/records?employeeId=&start=&end=` | 考勤记录查询 |
| GET | `/api/attendance/today` | 今日打卡记录 |

### 3.6 请假 Leave

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/leave/apply` | 提交请假申请 |
| GET | `/api/leave/my` | 我的请假列表 |
| GET | `/api/leave/pending` | 待我审批的列表 |
| PUT | `/api/leave/approve` | 审批（id, status: 已批准/已拒绝） |

### 3.7 薪资 Salary

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/salary?year=&month=` | 薪资列表 |
| POST | `/api/salary` | 新增薪资（自动算实发） |
| PUT | `/api/salary` | 修改薪资 |

### 3.8 仪表板 Dashboard

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/dashboard` | 汇总指标（员工总数/在职/新入职/部门数/待审批/今日打卡/迟到） |

### 3.9 请求示例

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 携带 Token 访问员工列表
curl http://localhost:8080/api/employee \
  -H "Authorization: Bearer <token>"

# 打卡
curl -X POST http://localhost:8080/api/attendance/clock \
  -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"clockType":"IN"}'
```

---

## 4. 审查报告

### 4.1 代码审查结论

| 维度 | 结论 |
|------|------|
| 架构一致性 | 通过。分层清晰，domain 无框架依赖，符合 DDD 约束 |
| 数据一致性 | 通过。逻辑删除 + 唯一索引 + 业务层关联校验 |
| 安全 | 基本通过。BCrypt 存储密码、Token 认证、接口白名单；建议后续补充基于角色的细粒度权限 |
| 异常处理 | 通过。全局异常统一返回，业务异常与系统异常分离 |
| 可维护性 | 通过。命名规范、DTO/实体/VO 分离、注释完整 |

### 4.2 审查中发现并修复的问题

| # | 问题 | 根因 | 修复 |
|---|------|------|------|
| 1 | 后端启动 Bean 冲突 | `WebSocketHandler` 同时有 `@Component` 与 `@Bean` 注册 | 移除类上的 `@Component` 注解 |
| 2 | `ClassNotFoundException: jakarta.servlet` | Spring Boot 2.7 为 javax，误用 jakarta 版 knife4j | 替换为 `knife4j-openapi3-spring-boot-starter` |
| 3 | 数据库连接报错 | JDBC URL `characterEncoding=utf8mb4` 不被支持 | 改为 `utf8`，加 `serverTimezone=Asia/Shanghai` |
| 4 | admin 登录失败 | 初始密码哈希错误 | 重新生成正确的 BCrypt 密文 |
| 5 | SQL 语法错误 | `position.rank`、`attendance_record.date` 为 MySQL 保留字 | 字段加反引号 `@TableField("\`rank\`")` |
| 6 | `ClassCastException: String→Long` | `StpUtil.getLoginId()` 返回 String | 改用 `StpUtil.getLoginIdAsLong()` |
| 7 | 下班打卡主键冲突 | 已有记录仍走 insert | 按记录是否有 ID 选择 update/save |
| 8 | 前端菜单点击无响应 | 路由 name 大小写不匹配 + 依赖未更新的 selectedKeys | 菜单 key 映射大写路由 name，使用点击回调参数 |
| 9 | 新增员工工号不可填 | 输入框被 `disabled` 而后端校验必填 | 去掉 disabled，支持手动填写并保留唯一校验 |

### 4.3 遗留建议

- **权限细化**：目前接口仅做登录校验，建议按角色（admin/hr/supervisor/employee）增加接口级权限码控制。
- **密码策略**：建议增加登录失败锁定、密码强度校验。
- **考勤统计**：月度考勤统计表（`attendance_monthly_stat`）暂无触发填充逻辑，建议补充定时任务。
- **前端路由守卫**：当前仅校验是否登录，未校验角色页面访问权限，建议完善。
