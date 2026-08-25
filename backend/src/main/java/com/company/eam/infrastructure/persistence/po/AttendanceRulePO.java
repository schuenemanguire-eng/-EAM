package com.company.eam.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("attendance_rule")
public class AttendanceRulePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("work_start_time")
    private LocalTime workStartTime;

    @TableField("work_end_time")
    private LocalTime workEndTime;

    @TableField("late_threshold_minutes")
    private Integer lateThresholdMinutes;

    @TableField("early_leave_threshold")
    private Integer earlyLeaveThreshold;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
