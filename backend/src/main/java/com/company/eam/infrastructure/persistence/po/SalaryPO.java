package com.company.eam.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("salary")
public class SalaryPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    private Integer year;

    private Integer month;

    @TableField("base_salary")
    private BigDecimal baseSalary;

    private BigDecimal bonus;

    private BigDecimal allowance;

    private BigDecimal deduction;

    @TableField("total_salary")
    private BigDecimal totalSalary;

    @TableField("pay_date")
    private LocalDate payDate;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
