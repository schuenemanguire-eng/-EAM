package com.company.eam.application.service;

import cn.hutool.core.bean.BeanUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.company.eam.api.dto.request.LeaveApplicationRequest;
import com.company.eam.api.dto.request.LeaveApprovalRequest;
import com.company.eam.api.dto.response.LeaveApplicationVO;
import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.*;
import com.company.eam.domain.repository.*;
import com.company.eam.domain.service.LeaveDomainService;
import com.company.eam.infrastructure.config.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveAppService {

    @Autowired private LeaveApplicationRepository leaveRepository;
    @Autowired private LeaveDomainService leaveDomainService;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DeptRepository deptRepository;

    public void applyLeave(LeaveApplicationRequest req) {
        Long userId = StpUtil.getLoginIdAsLong();
        UserEntity user = userRepository.findById(userId);
        if (user == null || user.getEmployeeId() == null) {
            throw new BusinessException("当前用户未关联员工");
        }
        Long employeeId = user.getEmployeeId();
        BigDecimal days = leaveDomainService.calculateLeaveDays(req.getStartTime(), req.getEndTime());
        Long approverId = leaveDomainService.getApproverId(employeeId);

        LeaveApplication app = BeanUtil.copyProperties(req, LeaveApplication.class);
        app.setEmployeeId(employeeId);
        app.setTotalDays(days);
        app.setApproverId(approverId);
        app.setStatus("待审批");
        leaveRepository.save(app);

        // 通知审批人
        try {
            UserEntity approver = userRepository.findByEmployeeId(approverId);
            if (approver != null) {
                WebSocketHandler.send(approver.getUsername(), "您有新的请假审批: " + employeeId);
            }
        } catch (Exception ignored) {}
    }

    public List<LeaveApplicationVO> listByEmployee(Long employeeId) {
        List<LeaveApplication> list = leaveRepository.findByEmployeeId(employeeId);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    public List<LeaveApplicationVO> listPending(Long approverId) {
        List<LeaveApplication> list = leaveRepository.findByApproverId(approverId);
        return list.stream()
                .filter(l -> "待审批".equals(l.getStatus()))
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    public void approve(LeaveApprovalRequest req) {
        LeaveApplication app = leaveRepository.findById(req.getId());
        if (app == null) throw new BusinessException("请假申请不存在");

        Long userId = StpUtil.getLoginIdAsLong();
        UserEntity user = userRepository.findById(userId);
        if (user == null || user.getEmployeeId() == null) {
            throw new BusinessException("当前用户未关联员工");
        }
        if (!user.getEmployeeId().equals(app.getApproverId())) {
            throw new BusinessException("您不是该申请的审批人");
        }

        app.setStatus(req.getStatus());
        app.setApproveTime(LocalDateTime.now());
        leaveRepository.update(app);

        // 通知申请人
        try {
            UserEntity applicant = userRepository.findByEmployeeId(app.getEmployeeId());
            if (applicant != null) {
                WebSocketHandler.send(applicant.getUsername(),
                        "请假申请已" + req.getStatus());
            }
        } catch (Exception ignored) {}
    }

    private LeaveApplicationVO toVO(LeaveApplication app) {
        LeaveApplicationVO vo = BeanUtil.copyProperties(app, LeaveApplicationVO.class);
        EmployeeEntity emp = employeeRepository.findById(app.getEmployeeId());
        if (emp != null) vo.setEmployeeName(emp.getName());
        if (app.getApproverId() != null) {
            EmployeeEntity approver = employeeRepository.findById(app.getApproverId());
            if (approver != null) vo.setApproverName(approver.getName());
        }
        return vo;
    }
}
