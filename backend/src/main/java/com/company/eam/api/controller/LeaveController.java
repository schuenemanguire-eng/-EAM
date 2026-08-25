package com.company.eam.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.company.eam.api.dto.request.LeaveApplicationRequest;
import com.company.eam.api.dto.request.LeaveApprovalRequest;
import com.company.eam.api.dto.response.LeaveApplicationVO;
import com.company.eam.application.service.LeaveAppService;
import com.company.eam.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
@Tag(name = "请假管理")
public class LeaveController {

    @Autowired
    private LeaveAppService leaveAppService;

    @PostMapping("/apply")
    public Result<Void> apply(@Valid @RequestBody LeaveApplicationRequest req) {
        leaveAppService.applyLeave(req);
        return Result.ok();
    }

    @GetMapping("/my")
    public Result<List<LeaveApplicationVO>> myList() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.ok(leaveAppService.listByEmployee(userId));
    }

    @GetMapping("/pending")
    public Result<List<LeaveApplicationVO>> pending() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.ok(leaveAppService.listPending(userId));
    }

    @PutMapping("/approve")
    public Result<Void> approve(@Valid @RequestBody LeaveApprovalRequest req) {
        leaveAppService.approve(req);
        return Result.ok();
    }
}
