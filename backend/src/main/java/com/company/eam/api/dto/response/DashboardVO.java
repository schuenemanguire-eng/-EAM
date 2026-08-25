package com.company.eam.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVO {

    private Long totalEmployees;

    private Long activeEmployees;

    private Long newThisMonth;

    private Long totalDepartments;

    private Long pendingLeaves;

    private Long todayClockInCount;

    private Long lateCountToday;
}
