package com.company.eam.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("leave_application")
public class LeaveApplicationPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("leave_type")
    private String leaveType;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("total_days")
    private BigDecimal totalDays;

    private String reason;

    private String status;

    @TableField("approver_id")
    private Long approverId;

    @TableField("approve_time")
    private LocalDateTime approveTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
