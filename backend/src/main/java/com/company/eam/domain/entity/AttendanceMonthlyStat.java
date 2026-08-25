package com.company.eam.domain.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AttendanceMonthlyStat {
    private Long id;
    private Long employeeId;
    private Integer year;
    private Integer month;
    private Integer workDays;
    private Integer lateDays;
    private Integer earlyDays;
    private Integer leaveDays;
    private Integer actualWorkDays;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
