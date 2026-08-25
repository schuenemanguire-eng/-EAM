package com.company.eam.domain.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeEntity {
    private Long id;
    private String name;
    private String employeeNo;
    private Long deptId;
    private Long positionId;
    private LocalDate entryDate;
    private Integer status;
    private String phone;
    private String email;
    private Integer gender;
    private LocalDate birthday;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
