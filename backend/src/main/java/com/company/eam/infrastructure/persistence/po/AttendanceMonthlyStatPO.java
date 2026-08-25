package com.company.eam.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attendance_monthly_stat")
public class AttendanceMonthlyStatPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    private Integer year;

    private Integer month;

    @TableField("work_days")
    private Integer workDays;

    @TableField("late_days")
    private Integer lateDays;

    @TableField("early_days")
    private Integer earlyDays;

    @TableField("leave_days")
    private Integer leaveDays;

    @TableField("actual_work_days")
    private Integer actualWorkDays;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
