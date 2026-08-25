package com.company.eam.domain.entity;

import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class AttendanceRule {
    private Long id;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private Integer lateThresholdMinutes;
    private Integer earlyLeaveThreshold;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
