package com.company.eam.api.dto.request;

import javax.validation.constraints.NotNull;
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
public class SalaryRequest {

    private Long id;

    @NotNull
    private Long employeeId;

    @NotNull
    private Integer year;

    @NotNull
    private Integer month;

    private BigDecimal baseSalary;

    private BigDecimal bonus;

    private BigDecimal allowance;

    private BigDecimal deduction;

    private LocalDate payDate;
}
