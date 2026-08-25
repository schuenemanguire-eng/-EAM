package com.company.eam.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("attendance_record")
public class AttendanceRecordPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("clock_in_time")
    private LocalDateTime clockInTime;

    @TableField("clock_out_time")
    private LocalDateTime clockOutTime;

    private String status;

    // date 是 MySQL 保留字，加反引号避免语法错误
    @TableField("`date`")
    private LocalDate date;

    private LocalDateTime createTime;
}
