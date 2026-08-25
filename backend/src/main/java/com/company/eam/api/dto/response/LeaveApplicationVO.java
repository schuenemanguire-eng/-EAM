package com.company.eam.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationVO {

    private Long id;

    private Long employeeId;

    private String employeeName;

    private String leaveType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal totalDays;

    private String reason;

    private String status;

    private String approverName;

    private LocalDateTime approveTime;
}
