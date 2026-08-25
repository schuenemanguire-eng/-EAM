package com.company.eam.domain.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private Long employeeId;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
