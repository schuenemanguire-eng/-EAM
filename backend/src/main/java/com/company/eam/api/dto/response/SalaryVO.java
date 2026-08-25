package com.company.eam.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryVO {

    private Long id;

    private Long employeeId;

    private String employeeName;

    private Integer year;

    private Integer month;

    private BigDecimal baseSalary;

    private BigDecimal bonus;

    private BigDecimal allowance;

    private BigDecimal deduction;

    private BigDecimal totalSalary;

    private LocalDate payDate;
}
