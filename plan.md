```
# 企业员工管理系统 —— 分步开发提示词（Agent IDE 专用）
> 重要强制规则：**必须严格按7个阶段顺序生成，完成一个阶段后输出文字【阶段X完成，请人工校验后继续下一阶段】，禁止一次性输出全部代码，等待确认再继续下一个阶段。所有代码尽量可运行，关键位置增加注释。**

## 一、项目总体说明
**项目名称**：EnterpriseEmployeeManagementSystem（企业员工管理系统）
**目标**：实现一套完整的员工信息管理、考勤、请假、薪资、数据看板功能，满足中小企业人事管理需求。
**技术选型（精简后）**：
- **后端**：Java 17 + Spring Boot 2.7.x + MySQL 8.0 + MyBatis‑Plus 3.5.x + MyBatis X（代码生成插件） + Sa‑Token（权限控制） + WebSocket（仅用于审批/打卡通知，单机内存会话） + JUC(@EnableAsync异步任务) + 常用设计模式（策略、工厂等） + 简化DDD分层。
- **前端**：Vue 3 + Vite + Ant Design Vue 4.x + Axios + Pinia + TypeScript + ESLint + Prettier + OpenAPI前端代码生成（后端SpringDoc‑Knife4j输出OpenAPI3规范，本地脚本生成，不是浏览器运行时生成）。
- **数据库**：MySQL8.0 InnoDB，字符集utf8mb4，**不使用数据库外键，所有关联校验在业务层实现，使用逻辑删除，不物理删除业务数据**。
- **其他**：Maven单模块，包结构按DDD分层；本地单机运行，不引入Redis、COS、分库分表、Disruptor、AI绘图等组件。
- 权限简化4种角色：`admin系统管理员`、`hr人事`、`supervisor部门主管`、`employee普通员工`。
- 审批：单级审批；**部门表存储manager_id为本部门主管员工ID；员工提交请假时自动赋值approver_id为部门manager_id**。

> DDD强制约束：domain领域层**不能引入MyBatis/Mysql相关依赖，领域实体是纯业务实体，不是数据库映射实体；数据库PO实体放置在infrastructure包；避免贫血DDD，核心业务规则写在domain领域服务内，application层做编排，repository仓储接口定义在domain，实现在infrastructure**。

---
## 二、开发步骤总览
| 阶段 | 名称 | 主要内容 |
|------|------|----------|
| 1 | 数据库设计与初始化 | 设计ER图，编写DDL脚本，创建所有数据表，包含初始测试数据 |
| 2 | 后端项目骨架与DDD分层 | 创建Spring Boot Maven项目，定义Maven依赖，搭建DDD包结构 |
| 3 | 基础通用组件 | 统一响应、全局异常、Sa‑Token配置、WebSocket配置、异步线程池、枚举类、工具类、密码加密 |
| 4 | 核心业务模块（逐个生成） | 部门、职位、员工、考勤、请假、薪资、仪表板（按顺序，每个模块包含DDD各层代码） |
| 5 | 权限控制完善 | 定义角色与权限码，Sa‑Token注解拦截，登录认证接口；角色权限映射**内存硬编码，不新建role/permission数据库表** |
| 6 | 前端工程搭建与页面开发 | Vite创建Vue3+TS项目，配置Ant Design、Pinia、Axios、路由，OpenAPI代码生成脚本，开发全部业务页面 |
| 7 | 集成测试与项目优化 | 编写简单集成测试，业务边界校验、查询优化、安全加固，输出README.md |

## 三、详细步骤与指令
### 阶段1：数据库设计与初始化
**任务**：设计所有数据表，输出完整schema.sql脚本，包含建库、建表、基础初始化数据。
**表清单（修正：dept增加manager_id，全部开启MyBatis‑Plus逻辑删除字段deleted）**
1. `dept`（部门表）: id, name, parent_id, manager_id【部门主管员工ID】, sort, status, deleted, create_time, update_time
2. `position`（职位表）: id, name, dept_id, rank（职级P1‑P8）, deleted, create_time, update_time
3. `employee`（员工表）: id, name, employee_no（工号唯一）, dept_id, position_id, entry_date, status(在职/离职), phone, email, gender, birthday, deleted, create_time, update_time
4. `user`（用户表，关联员工）: id, username, password【BCrypt密文存储】, employee_id, role(employee/supervisor/hr/admin), status, deleted, create_time, update_time
5. `attendance_rule`（考勤规则表HR配置）: id, work_start_time, work_end_time, late_threshold_minutes, early_leave_threshold, create_time, update_time，业务上只保留1条配置记录
6. `attendance_record`（打卡记录表）: id, employee_id, clock_in_time, clock_out_time, status(正常/迟到/早退/缺卡), date, create_time
7. `leave_application`（请假申请表）: id, employee_id, leave_type, start_time, end_time, total_days, reason, status(待审批/已批准/已拒绝), approver_id, approve_time, create_time, update_time
8. `salary`（薪资表）: id, employee_id, year, month, base_salary, bonus, allowance, deduction, total_salary, pay_date, deleted, create_time, update_time；**唯一约束：同一个员工同年月只能一条薪资记录**
9. `attendance_monthly_stat`（月度考勤统计表）: id, employee_id, year, month, work_days, late_days, early_days, leave_days, actual_work_days, create_time, update_time

**索引要求**
- employee_no 唯一索引
- attendance_record：(employee_id, date)联合唯一索引，同一天员工只能一条打卡记录
- leave_application：(employee_id, status)普通索引
- salary：(employee_id,year,month)唯一索引

**业务约束**
> 不建立数据库外键，全部业务层校验关联；全部业务表实现逻辑删除（deleted字段），禁止物理删除员工、部门等核心业务数据。
**初始化数据：内置管理员账号 admin，密码123456；内置根部门记录，基础职位数据。**
输出完整schema.sql脚本。

### 阶段2：后端项目骨架与DDD分层
**项目包结构（单模块）**

```

