package com.company.eam.application.service;

import cn.hutool.core.bean.BeanUtil;
import com.company.eam.api.dto.request.AttendanceClockRequest;
import com.company.eam.api.dto.response.AttendanceRecordVO;
import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.AttendanceRecord;
import com.company.eam.domain.entity.AttendanceRule;
import com.company.eam.domain.entity.EmployeeEntity;
import com.company.eam.domain.entity.UserEntity;
import com.company.eam.domain.enums.AttendanceStatusEnum;
import com.company.eam.domain.repository.AttendanceRecordRepository;
import com.company.eam.domain.repository.AttendanceRuleRepository;
import com.company.eam.domain.repository.EmployeeRepository;
import com.company.eam.domain.repository.UserRepository;
import com.company.eam.domain.service.AttendanceDomainService;
import com.company.eam.infrastructure.config.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceAppService {

    @Autowired
    private AttendanceRecordRepository recordRepository;

    @Autowired
    private AttendanceRuleRepository ruleRepository;

    @Autowired
    private AttendanceDomainService attendanceDomainService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public void clock(AttendanceClockRequest req) {
        Long userId = getLoggedInUserId();
        UserEntity user = userRepository.findById(userId);
        if (user == null || user.getEmployeeId() == null) {
            throw new BusinessException("当前用户未关联员工信息");
        }
        Long employeeId = user.getEmployeeId();
        LocalDate date = req.getDate() != null ? req.getDate() : LocalDate.now();

        AttendanceRecord existing = recordRepository.findByEmployeeIdAndDate(employeeId, date);
        if (existing != null) {
            if ("in".equals(req.getClockType())) {
                throw new BusinessException("今日已上班打卡");
            }
            existing.setClockOutTime(LocalDateTime.now());
        } else {
            if ("out".equals(req.getClockType())) {
                throw new BusinessException("请先上班打卡");
            }
            existing = new AttendanceRecord();
            existing.setEmployeeId(employeeId);
            existing.setDate(date);
            existing.setClockInTime(LocalDateTime.now());
        }

        // 判断考勤状态
        AttendanceRule rule = ruleRepository.findByPrimaryKey().orElse(null);
        if (rule != null) {
            existing.setStatus(attendanceDomainService.judgeAttendanceStatus(
                    existing.getClockInTime(), existing.getClockOutTime(), rule));
        }

        // 已有记录（如补记下班卡）走 update，避免主键/唯一索引冲突；否则走 insert
        if (existing.getId() != null) {
            recordRepository.update(existing);
        } else {
            recordRepository.save(existing);
        }

        // WebSocket通知
        try {
            WebSocketHandler.send("Bearer " + getLoggedInToken(),
                    "打卡成功: " + existing.getStatus());
        } catch (Exception e) {
            // WebSocket发送失败不影响主流程
        }
    }

    public List<AttendanceRecordVO> listRecords(Long employeeId, LocalDate start, LocalDate end) {
        List<AttendanceRecord> records;
        if (start != null && end != null) {
            records = recordRepository.findByEmployeeIdAndDateRange(employeeId, start, end);
        } else if (employeeId != null) {
            records = recordRepository.findByEmployeeId(employeeId);
        } else {
            return List.of();
        }
        return records.stream().map(r -> {
            AttendanceRecordVO vo = BeanUtil.copyProperties(r, AttendanceRecordVO.class);
            EmployeeEntity emp = employeeRepository.findById(r.getEmployeeId());
            if (emp != null) vo.setEmployeeName(emp.getName());
            return vo;
        }).collect(Collectors.toList());
    }

    public AttendanceRecordVO getTodayRecord() {
        Long userId = getLoggedInUserId();
        UserEntity user = userRepository.findById(userId);
        if (user == null || user.getEmployeeId() == null) return null;
        AttendanceRecord r = recordRepository.findByEmployeeIdAndDate(user.getEmployeeId(), LocalDate.now());
        if (r == null) return null;
        AttendanceRecordVO vo = BeanUtil.copyProperties(r, AttendanceRecordVO.class);
        EmployeeEntity emp = employeeRepository.findById(r.getEmployeeId());
        if (emp != null) vo.setEmployeeName(emp.getName());
        return vo;
    }

    private Long getLoggedInUserId() {
        return cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
    }

    private String getLoggedInToken() {
        return cn.dev33.satoken.stp.StpUtil.getTokenValue();
    }
}
