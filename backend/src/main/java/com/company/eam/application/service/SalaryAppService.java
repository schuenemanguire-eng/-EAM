package com.company.eam.application.service;

import cn.hutool.core.bean.BeanUtil;
import com.company.eam.api.dto.request.SalaryRequest;
import com.company.eam.api.dto.response.SalaryVO;
import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.EmployeeEntity;
import com.company.eam.domain.entity.Salary;
import com.company.eam.domain.repository.EmployeeRepository;
import com.company.eam.domain.repository.SalaryRepository;
import com.company.eam.domain.service.SalaryDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryAppService {

    @Autowired private SalaryRepository salaryRepository;
    @Autowired private SalaryDomainService salaryDomainService;
    @Autowired private EmployeeRepository employeeRepository;

    public List<SalaryVO> listSalaries(Integer year, Integer month, Long employeeId) {
        List<Salary> list;
        if (employeeId != null) {
            list = salaryRepository.findByEmployeeId(employeeId);
        } else if (year != null && month != null) {
            list = salaryRepository.findByYearMonth(year, month);
        } else {
            list = salaryRepository.findAll();
        }
        return list.stream().map(s -> {
            SalaryVO vo = BeanUtil.copyProperties(s, SalaryVO.class);
            EmployeeEntity emp = employeeRepository.findById(s.getEmployeeId());
            if (emp != null) vo.setEmployeeName(emp.getName());
            return vo;
        }).collect(Collectors.toList());
    }

    public void createSalary(SalaryRequest req) {
        salaryDomainService.validateSalaryUnique(req.getEmployeeId(), req.getYear(), req.getMonth());
        BigDecimal total = salaryDomainService.calculateTotalSalary(
                req.getBaseSalary(), req.getBonus(), req.getAllowance(), req.getDeduction());
        Salary salary = BeanUtil.copyProperties(req, Salary.class);
        salary.setTotalSalary(total);
        salaryRepository.save(salary);
    }

    public void updateSalary(SalaryRequest req) {
        Salary salary = salaryRepository.findByEmployeeIdAndYearMonth(
                req.getEmployeeId(), req.getYear(), req.getMonth()).orElse(null);
        if (salary == null) throw new BusinessException("薪资记录不存在");
        BigDecimal total = salaryDomainService.calculateTotalSalary(
                req.getBaseSalary(), req.getBonus(), req.getAllowance(), req.getDeduction());
        BeanUtil.copyProperties(req, salary);
        salary.setTotalSalary(total);
        salaryRepository.update(salary);
    }
}
