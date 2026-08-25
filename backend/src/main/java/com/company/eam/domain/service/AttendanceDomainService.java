package com.company.eam.domain.service;

import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.AttendanceRecord;
import com.company.eam.domain.entity.AttendanceRule;
import com.company.eam.domain.repository.AttendanceRecordRepository;
import com.company.eam.domain.repository.AttendanceRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Component
public class AttendanceDomainService {

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private AttendanceRuleRepository attendanceRuleRepository;

    /**
     * Validate that there is no duplicate clock record for the given employee and date.
     *
     * @throws BusinessException if a record already exists for that employee on that date
     */
    public void validateDuplicateClock(Long employeeId, LocalDate date) {
        if (employeeId == null || date == null) {
            throw new BusinessException("员工ID和日期不能为空");
        }
        AttendanceRecord existing = attendanceRecordRepository.findByEmployeeIdAndDate(employeeId, date);
        if (existing != null) {
            log.warn("Duplicate attendance record, employeeId={}, date={}", employeeId, date);
            throw new BusinessException("当天打卡记录已存在");
        }
    }

    /**
     * Judge attendance status based on clock-in and clock-out times against the attendance rule.
     *
     * @return "缺卡" if clockIn or clockOut is missing;
     *         "正常" if on time;
     *         "迟到" if clocked in late (exceeding threshold);
     *         "早退" if clocked out early (exceeding threshold)
     */
    public String judgeAttendanceStatus(LocalDateTime clockIn, LocalDateTime clockOut, AttendanceRule rule) {
        if (clockIn == null || clockOut == null) {
            return "缺卡";
        }

        if (rule == null) {
            return "正常";
        }

        LocalTime clockInTime = clockIn.toLocalTime();
        LocalTime clockOutTime = clockOut.toLocalTime();

        int lateThreshold = rule.getLateThresholdMinutes() != null ? rule.getLateThresholdMinutes() : 0;
        int earlyThreshold = rule.getEarlyLeaveThreshold() != null ? rule.getEarlyLeaveThreshold() : 0;

        // Check late (clock in after work start time + threshold)
        if (clockInTime.isAfter(rule.getWorkStartTime().plusMinutes(lateThreshold))) {
            return "迟到";
        }

        // Check early leave (clock out before work end time - threshold)
        if (clockOutTime.isBefore(rule.getWorkEndTime().minusMinutes(earlyThreshold))) {
            return "早退";
        }

        return "正常";
    }
}
