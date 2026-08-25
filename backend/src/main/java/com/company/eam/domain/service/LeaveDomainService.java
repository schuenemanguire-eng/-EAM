package com.company.eam.domain.service;

import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.DeptEntity;
import com.company.eam.domain.entity.EmployeeEntity;
import com.company.eam.domain.entity.LeaveApplication;
import com.company.eam.domain.repository.DeptRepository;
import com.company.eam.domain.repository.EmployeeRepository;
import com.company.eam.domain.repository.LeaveApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;

@Slf4j
@Component
public class LeaveDomainService {

    private static final BigDecimal HOURS_PER_DAY = BigDecimal.valueOf(8);

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DeptRepository deptRepository;

    /**
     * Calculate the number of leave days (business days) between start and end time.
     * Uses the time-of-day to determine partial days:
     * <ul>
     *   <li>startTime before 12:00 counts as a half day; otherwise a full day</li>
     *   <li>endTime after 12:00 counts as a half day; otherwise rounds up to a full day</li>
     * </ul>
     */
    public BigDecimal calculateLeaveDays(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BusinessException("开始时间和结束时间不能为空");
        }
        if (!endTime.isAfter(startTime)) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }

        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        LocalTime startOfDay = startTime.toLocalTime();
        LocalTime endOfDay = endTime.toLocalTime();
        LocalTime noon = LocalTime.of(12, 0);

        BigDecimal totalDays = BigDecimal.ZERO;

        if (startDate.isEqual(endDate)) {
            Duration duration = Duration.between(startTime, endTime);
            long minutes = duration.toMinutes();
            BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            totalDays = hours.divide(HOURS_PER_DAY, 2, RoundingMode.HALF_UP);
        } else {
            // Start day partial
            if (startOfDay.isBefore(noon)) {
                totalDays = totalDays.add(BigDecimal.valueOf(0.5));
            } else {
                totalDays = totalDays.add(BigDecimal.ONE);
            }

            // End day partial
            if (endOfDay.isAfter(noon)) {
                totalDays = totalDays.add(BigDecimal.valueOf(0.5));
            } else {
                totalDays = totalDays.add(BigDecimal.ONE);
            }

            // Full business days in between
            LocalDate current = startDate.plusDays(1);
            while (current.isBefore(endDate)) {
                if (current.getDayOfWeek() != DayOfWeek.SATURDAY && current.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    totalDays = totalDays.add(BigDecimal.ONE);
                }
                current = current.plusDays(1);
            }
        }

        return totalDays;
    }

    /**
     * Find the approver (manager) for a given employee by looking up the employee's department.
     *
     * @throws BusinessException if the employee or department is not found, or has no manager
     */
    public Long getApproverId(Long employeeId) {
        EmployeeEntity employee = employeeRepository.findById(employeeId);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        DeptEntity dept = deptRepository.findById(employee.getDeptId());
        if (dept == null) {
            throw new BusinessException("员工所属部门不存在");
        }
        if (dept.getManagerId() == null) {
            throw new BusinessException("部门未设置负责人，无法审批");
        }
        return dept.getManagerId();
    }

    /**
     * Validate that a leave application exists and the current user is the assigned approver.
     *
     * @throws BusinessException if the application is not found or the approver does not match
     */
    public void validateLeaveApplication(Long id, Long approverId) {
        LeaveApplication leave = leaveApplicationRepository.findById(id);
        if (leave == null) {
            log.warn("Leave application not found, id={}", id);
            throw new BusinessException("请假申请不存在");
        }
        if (!leave.getApproverId().equals(approverId)) {
            log.warn("Approver mismatch, leaveId={}, approverId={}, requiredApproverId={}", id, approverId, leave.getApproverId());
            throw new BusinessException("该申请不属于您，无法审批");
        }
    }
}
