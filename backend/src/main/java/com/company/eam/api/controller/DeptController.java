package com.company.eam.api.controller;

import com.company.eam.api.dto.request.DeptRequest;
import com.company.eam.api.dto.response.DeptVO;
import com.company.eam.application.service.DeptAppService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dept")
@Tag(name = "部门管理")
public class DeptController {

    @Autowired
    private DeptAppService deptAppService;

    @GetMapping("/tree")
    public Result<List<DeptVO>> getTree() {
        return Result.ok(deptAppService.getDeptTree());
    }

    @GetMapping("/{id}")
    public Result<DeptVO> getById(@PathVariable Long id) {
        return Result.ok(deptAppService.getDept(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody DeptRequest req) {
        deptAppService.createDept(req);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody DeptRequest req) {
        deptAppService.updateDept(req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deptAppService.deleteDept(id);
        return Result.ok();
    }
}
