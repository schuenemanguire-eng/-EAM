package com.company.eam.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecordVO {

    private Long id;

    private Long employeeId;

    private String employeeName;

    private LocalDateTime clockInTime;

    private LocalDateTime clockOutTime;

    private String status;

    private LocalDate date;
}
