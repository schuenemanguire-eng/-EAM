package com.company.eam.domain.service;

import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.Salary;
import com.company.eam.domain.repository.SalaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Component
public class SalaryDomainService {

    @Autowired
    private SalaryRepository salaryRepository;

    /**
     * Validate that no salary record already exists for the same employee, year, and month.
     *
     * @throws BusinessException if a salary record already exists for the given employee/year/month
     */
    public void validateSalaryUnique(Long employeeId, Integer year, Integer month) {
        if (employeeId == null || year == null || month == null) {
            throw new BusinessException("员工ID、年份、月份不能为空");
        }
        Optional<Salary> existing = salaryRepository.findByEmployeeIdAndYearMonth(employeeId, year, month);
        if (existing.isPresent()) {
            log.warn("Salary record already exists, employeeId={}, year={}, month={}", employeeId, year, month);
            throw new BusinessException("该员工当月薪资记录已存在");
        }
    }

    /**
     * Calculate the total salary: base + bonus + allowance - deduction.
     */
    public BigDecimal calculateTotalSalary(BigDecimal base, BigDecimal bonus, BigDecimal allowance, BigDecimal deduction) {
        BigDecimal b = base != null ? base : BigDecimal.ZERO;
        BigDecimal bn = bonus != null ? bonus : BigDecimal.ZERO;
        BigDecimal al = allowance != null ? allowance : BigDecimal.ZERO;
        BigDecimal dd = deduction != null ? deduction : BigDecimal.ZERO;

        return b.add(bn).add(al).subtract(dd);
    }
}
