package com.company.eam.api.controller;

import com.company.eam.api.dto.request.EmployeeRequest;
import com.company.eam.api.dto.response.EmployeeVO;
import com.company.eam.application.service.EmployeeAppService;
import com.company.eam.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@Tag(name = "员工管理")
public class EmployeeController {

    @Autowired
    private EmployeeAppService employeeAppService;

    @GetMapping
    public Result<List<EmployeeVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long deptId) {
        return Result.ok(employeeAppService.listEmployees(keyword, deptId));
    }

    @GetMapping("/{id}")
    public Result<EmployeeVO> getById(@PathVariable Long id) {
        return Result.ok(employeeAppService.getEmployee(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody EmployeeRequest req) {
        employeeAppService.createEmployee(req);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody EmployeeRequest req) {
        employeeAppService.updateEmployee(req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeAppService.deleteEmployee(id);
        return Result.ok();
    }

    @PutMapping("/{id}/quit")
    public Result<Void> quit(@PathVariable Long id) {
        employeeAppService.quit(id);
        return Result.ok();
    }
}
