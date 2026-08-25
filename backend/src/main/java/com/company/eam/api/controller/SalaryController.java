package com.company.eam.api.controller;

import com.company.eam.api.dto.request.SalaryRequest;
import com.company.eam.api.dto.response.SalaryVO;
import com.company.eam.application.service.SalaryAppService;
import com.company.eam.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
@Tag(name = "薪资管理")
public class SalaryController {

    @Autowired
    private SalaryAppService salaryAppService;

    @GetMapping
    public Result<List<SalaryVO>> list(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Long employeeId) {
        return Result.ok(salaryAppService.listSalaries(year, month, employeeId));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody SalaryRequest req) {
        salaryAppService.createSalary(req);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody SalaryRequest req) {
        salaryAppService.updateSalary(req);
        return Result.ok();
    }
}
