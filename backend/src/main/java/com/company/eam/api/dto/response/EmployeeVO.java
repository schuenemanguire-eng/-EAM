package com.company.eam.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeVO {

    private Long id;

    private String name;

    private String employeeNo;

    private Long deptId;

    private String deptName;

    private Long positionId;

    private String positionName;

    private LocalDate entryDate;

    private Integer status;

    private String phone;

    private String email;

    private Integer gender;

    private LocalDate birthday;
}
