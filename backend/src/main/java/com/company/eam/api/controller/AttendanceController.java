package com.company.eam.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.company.eam.api.dto.request.AttendanceClockRequest;
import com.company.eam.api.dto.response.AttendanceRecordVO;
import com.company.eam.application.service.AttendanceAppService;
import com.company.eam.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@Tag(name = "考勤管理")
public class AttendanceController {

    @Autowired
    private AttendanceAppService attendanceAppService;

    @PostMapping("/clock")
    public Result<Void> clock(@Valid @RequestBody AttendanceClockRequest req) {
        attendanceAppService.clock(req);
        return Result.ok();
    }

    @GetMapping("/records")
    public Result<List<AttendanceRecordVO>> listRecords(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        LocalDate startDate = (start != null && !start.isEmpty()) ? LocalDate.parse(start) : null;
        LocalDate endDate = (end != null && !end.isEmpty()) ? LocalDate.parse(end) : null;
        return Result.ok(attendanceAppService.listRecords(employeeId, startDate, endDate));
    }

    @GetMapping("/today")
    public Result<AttendanceRecordVO> todayRecord() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.ok(attendanceAppService.listRecords(userId, LocalDate.now(), LocalDate.now()).stream()
                .findFirst().orElse(null));
    }
}
