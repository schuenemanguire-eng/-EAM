package com.company.eam.domain.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class AttendanceRecord {
    private Long id;
    private Long employeeId;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private String status;
    private LocalDate date;
    private LocalDateTime createTime;
}
