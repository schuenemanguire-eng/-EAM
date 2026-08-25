package com.company.eam.application.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.company.eam.api.dto.request.LoginRequest;
import com.company.eam.api.dto.request.UserUpdateRequest;
import com.company.eam.api.dto.response.LoginResponse;
import com.company.eam.api.dto.response.UserVO;
import com.company.eam.common.BusinessException;
import com.company.eam.common.util.PasswordUtil;
import com.company.eam.domain.entity.EmployeeEntity;
import com.company.eam.domain.entity.UserEntity;
import com.company.eam.domain.repository.EmployeeRepository;
import com.company.eam.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public LoginResponse login(LoginRequest req) {
        UserEntity user = userRepository.findByUsername(req.getUsername());
        if (user == null || !PasswordUtil.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        if (user.getEmployeeId() != null) {
            EmployeeEntity emp = employeeRepository.findById(user.getEmployeeId());
            if (emp != null) {
                userVO.setEmployeeName(emp.getName());
            }
        }
        LoginResponse response = LoginResponse.builder().token(token).user(userVO).build();
        return response;
    }

    public UserVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserEntity user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        if (user.getEmployeeId() != null) {
            EmployeeEntity emp = employeeRepository.findById(user.getEmployeeId());
            if (emp != null) {
                userVO.setEmployeeName(emp.getName());
            }
        }
        return userVO;
    }

    public void logout() {
        StpUtil.logout();
    }

    public void updateProfile(UserUpdateRequest req) {
        UserEntity user = userRepository.findById(req.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getStatus() != null) user.setStatus(req.getStatus());
        userRepository.update(user);
    }
}
