package com.company.eam.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.company.eam.api.dto.request.LoginRequest;
import com.company.eam.api.dto.request.UserUpdateRequest;
import com.company.eam.api.dto.response.LoginResponse;
import com.company.eam.api.dto.response.UserVO;
import com.company.eam.application.service.AuthService;
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

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(authService.login(req));
    }

    @GetMapping("/currentUser")
    public Result<UserVO> getCurrentUser() {
        return Result.ok(authService.getCurrentUser());
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.ok();
    }

    @PutMapping("/updateProfile")
    public Result<Void> updateProfile(@Valid @RequestBody UserUpdateRequest req) {
        authService.updateProfile(req);
        return Result.ok();
    }
}
