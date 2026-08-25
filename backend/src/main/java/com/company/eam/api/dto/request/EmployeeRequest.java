package com.company.eam.api.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String employeeNo;

    private Long deptId;

    private Long positionId;

    private LocalDate entryDate;

    @Builder.Default
    private Integer status = 1;

    private String phone;

    private String email;

    private Integer gender;

    private LocalDate birthday;
}
