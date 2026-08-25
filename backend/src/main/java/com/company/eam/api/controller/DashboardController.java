package com.company.eam.api.controller;

import com.company.eam.api.dto.request.DashboardQuery;
import com.company.eam.api.dto.response.DashboardVO;
import com.company.eam.application.service.DashboardAppService;
import com.company.eam.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "仪表板")
public class DashboardController {

    @Autowired
    private DashboardAppService dashboardAppService;

    @GetMapping
    public Result<DashboardVO> getDashboard(DashboardQuery query) {
        return Result.ok(dashboardAppService.getDashboard(query));
    }
}
