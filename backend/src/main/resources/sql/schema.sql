-- ============================================================
-- 企业员工管理系统 —— 数据库初始化脚本
-- MySQL 8.0 / InnoDB / utf8mb4
-- 不使用外键约束，逻辑删除，全部业务校验在应用层
-- 所有用户密码为 123456 (BCrypt密文)
-- ============================================================

DROP DATABASE IF EXISTS eam_db;
CREATE DATABASE eam_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eam_db;

-- 1. 部门表
CREATE TABLE `dept` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID，0表示根部门',
  `manager_id` BIGINT DEFAULT NULL COMMENT '部门主管员工ID',
  `sort` INT DEFAULT 0 COMMENT '排序号',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 2. 职位表
CREATE TABLE `position` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '职位名称',
  `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
  `rank` VARCHAR(10) DEFAULT NULL COMMENT '职级 P1-P8',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';

-- 3. 员工表
CREATE TABLE `employee` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
  `employee_no` VARCHAR(32) NOT NULL COMMENT '工号(唯一)',
  `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
  `position_id` BIGINT DEFAULT NULL COMMENT '职位ID',
  `entry_date` DATE DEFAULT NULL COMMENT '入职日期',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1在职 0离职',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `gender` TINYINT DEFAULT NULL COMMENT '性别 1男 2女',
  `birthday` DATE DEFAULT NULL COMMENT '出生日期',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_employee_no` (`employee_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 4. 用户表
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(200) NOT NULL COMMENT 'BCrypt密文密码',
  `employee_id` BIGINT DEFAULT NULL COMMENT '关联员工ID',
  `role` VARCHAR(20) NOT NULL COMMENT '角色 admin/hr/supervisor/employee',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 5. 考勤规则表（业务上只保留1条）
CREATE TABLE `attendance_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_start_time` TIME NOT NULL COMMENT '上班时间',
  `work_end_time` TIME NOT NULL COMMENT '下班时间',
  `late_threshold_minutes` INT DEFAULT 0 COMMENT '迟到阈值(分钟)',
  `early_leave_threshold` INT DEFAULT 0 COMMENT '早退阈值(分钟)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤规则表';

-- 6. 打卡记录表
CREATE TABLE `attendance_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT NOT NULL COMMENT '员工ID',
  `clock_in_time` DATETIME DEFAULT NULL COMMENT '上班打卡时间',
  `clock_out_time` DATETIME DEFAULT NULL COMMENT '下班打卡时间',
  `status` VARCHAR(20) DEFAULT NULL COMMENT '状态 正常/迟到/早退/缺卡',
  `date` DATE NOT NULL COMMENT '考勤日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_emp_date` (`employee_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录表';

-- 7. 请假申请表
CREATE TABLE `leave_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT NOT NULL COMMENT '申请人员工ID',
  `leave_type` VARCHAR(20) NOT NULL COMMENT '请假类型',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `total_days` DECIMAL(4,2) NOT NULL COMMENT '请假天数',
  `reason` VARCHAR(500) DEFAULT NULL COMMENT '请假原因',
  `status` VARCHAR(20) NOT NULL DEFAULT '待审批' COMMENT '待审批/已批准/已拒绝',
  `approver_id` BIGINT DEFAULT NULL COMMENT '审批人员工ID',
  `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_emp_status` (`employee_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

-- 8. 薪资表
CREATE TABLE `salary` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT NOT NULL COMMENT '员工ID',
  `year` INT NOT NULL COMMENT '年份',
  `month` INT NOT NULL COMMENT '月份',
  `base_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
  `bonus` DECIMAL(10,2) DEFAULT 0 COMMENT '奖金',
  `allowance` DECIMAL(10,2) DEFAULT 0 COMMENT '津贴',
  `deduction` DECIMAL(10,2) DEFAULT 0 COMMENT '扣款',
  `total_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '实发工资',
  `pay_date` DATE DEFAULT NULL COMMENT '发放日期',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_emp_year_month` (`employee_id`, `year`, `month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资表';

-- 9. 月度考勤统计表
CREATE TABLE `attendance_monthly_stat` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` BIGINT NOT NULL COMMENT '员工ID',
  `year` INT NOT NULL COMMENT '年份',
  `month` INT NOT NULL COMMENT '月份',
  `work_days` INT DEFAULT 0 COMMENT '应出勤天数',
  `late_days` INT DEFAULT 0 COMMENT '迟到次数',
  `early_days` INT DEFAULT 0 COMMENT '早退次数',
  `leave_days` INT DEFAULT 0 COMMENT '请假天数',
  `actual_work_days` INT DEFAULT 0 COMMENT '实际出勤天数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月度考勤统计表';

-- ============================================================
-- 初始化数据
-- BCrypt密文对应明文密码: 123456
-- ============================================================
-- 根部门
INSERT INTO `dept` (`id`, `name`, `parent_id`, `manager_id`, `sort`, `status`) VALUES
(1, '总公司', 0, 1, 1, 1),
(2, '研发部', 1, 2, 1, 1),
(3, '人事部', 1, 3, 2, 1),
(4, '市场部', 1, 4, 3, 1);

-- 基础职位
INSERT INTO `position` (`id`, `name`, `dept_id`, `rank`) VALUES
(1, 'CEO', 1, 'P8'),
(2, '技术总监', 2, 'P7'),
(3, '人事主管', 3, 'P5'),
(4, '市场主管', 4, 'P5'),
(5, '高级开发工程师', 2, 'P4'),
(6, '初级开发工程师', 2, 'P2'),
(7, '人事专员', 3, 'P2'),
(8, '市场专员', 4, 'P2');

-- 员工
INSERT INTO `employee` (`id`, `name`, `employee_no`, `dept_id`, `position_id`, `entry_date`, `status`, `phone`, `email`, `gender`, `birthday`) VALUES
(1, '张总', 'EMP001', 1, 1, '2020-01-15', 1, '13800000001', 'zhang@example.com', 1, '1980-05-20'),
(2, '李经理', 'EMP002', 2, 2, '2020-03-01', 1, '13800000002', 'li@example.com', 1, '1985-08-10'),
(3, '王主管', 'EMP003', 3, 3, '2021-06-15', 1, '13800000003', 'wang@example.com', 2, '1990-12-01'),
(4, '赵主管', 'EMP004', 4, 4, '2021-07-01', 1, '13800000004', 'zhao@example.com', 1, '1988-03-25'),
(5, '刘工程师', 'EMP005', 2, 5, '2022-01-10', 1, '13800000005', 'liu@example.com', 1, '1993-11-15'),
(6, '陈工程师', 'EMP006', 2, 5, '2022-03-20', 1, '13800000006', 'chen@example.com', 2, '1995-07-08'),
(7, '周专员', 'EMP007', 3, 7, '2022-05-01', 1, '13800000007', 'zhou@example.com', 2, '1996-02-14'),
(8, '吴专员', 'EMP008', 4, 8, '2022-08-15', 1, '13800000008', 'wu@example.com', 1, '1997-09-30'),
(9, '孙开发', 'EMP009', 2, 6, '2023-02-01', 1, '13800000009', 'sun@example.com', 1, '1999-01-20'),
(10, '钱开发', 'EMP010', 2, 6, '2023-04-10', 1, '13800000010', 'qian@example.com', 2, '2000-06-15');

-- 用户表（所有密码均为 123456 的 BCrypt 密文）
INSERT INTO `sys_user` (`id`, `username`, `password`, `employee_id`, `role`, `status`) VALUES
(1, 'admin',    '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 1, 'admin', 1),
(2, 'li_jl',    '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 2, 'supervisor', 1),
(3, 'wang_zg',  '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 3, 'hr', 1),
(4, 'zhao_zg',  '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 4, 'supervisor', 1),
(5, 'liu_gc',   '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 5, 'employee', 1),
(6, 'chen_gc',  '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 6, 'employee', 1),
(7, 'zhou_zy',  '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 7, 'employee', 1),
(8, 'wu_zy',    '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 8, 'employee', 1),
(9, 'sun_kf',   '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 9, 'employee', 1),
(10,'qian_kf',  '$2a$10$X.03f.QUTxKllwpAanrGZep6JIxB3.jcnrXfmcTzDkoMYI4SeSRSq', 10,'employee', 1);

-- 考勤规则
INSERT INTO `attendance_rule` (`id`, `work_start_time`, `work_end_time`, `late_threshold_minutes`, `early_leave_threshold`) VALUES
(1, '09:00:00', '18:00:00', 15, 15);
