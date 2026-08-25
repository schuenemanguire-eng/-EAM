package com.company.eam.domain.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Salary {
    private Long id;
    private Long employeeId;
    private Integer year;
    private Integer month;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal allowance;
    private BigDecimal deduction;
    private BigDecimal totalSalary;
    private LocalDate payDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
