package com.company.eam.application.service;

import com.company.eam.api.dto.request.DashboardQuery;
import com.company.eam.api.dto.response.DashboardVO;
import com.company.eam.domain.entity.AttendanceRecord;
import com.company.eam.domain.entity.EmployeeEntity;
import com.company.eam.domain.entity.LeaveApplication;
import com.company.eam.domain.repository.AttendanceRecordRepository;
import com.company.eam.domain.repository.DeptRepository;
import com.company.eam.domain.repository.EmployeeRepository;
import com.company.eam.domain.repository.LeaveApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class DashboardAppService {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private DeptRepository deptRepository;
    @Autowired private LeaveApplicationRepository leaveRepository;
    @Autowired private AttendanceRecordRepository attendanceRepository;

    public DashboardVO getDashboard(DashboardQuery query) {
        List<EmployeeEntity> allEmployees = employeeRepository.findAll();
        List<EmployeeEntity> activeEmployees = employeeRepository.findByStatus(1);
        YearMonth ym = YearMonth.of(query.getYear(), query.getMonth());

        long newThisMonth = activeEmployees.stream()
                .filter(e -> e.getEntryDate() != null &&
                        e.getEntryDate().getYear() == ym.getYear() &&
                        e.getEntryDate().getMonthValue() == ym.getMonthValue())
                .count();

        long totalDepts = deptRepository.findAll().size();
        long pendingLeaves = leaveRepository.findByStatus("待审批").size();
        LocalDate today = LocalDate.now();
        long todayClock = attendanceRepository.findByDate(today).size();
        long lateCount = attendanceRepository.findByDate(today).stream()
                .filter(r -> "迟到".equals(r.getStatus()))
                .count();

        DashboardVO vo = new DashboardVO();
        vo.setTotalEmployees((long) allEmployees.size());
        vo.setActiveEmployees((long) activeEmployees.size());
        vo.setNewThisMonth(newThisMonth);
        vo.setTotalDepartments(totalDepts);
        vo.setPendingLeaves(pendingLeaves);
        vo.setTodayClockInCount(todayClock);
        vo.setLateCountToday(lateCount);
        return vo;
    }
}
