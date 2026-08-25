package com.company.eam.domain.service;

import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.DeptEntity;
import com.company.eam.domain.entity.EmployeeEntity;
import com.company.eam.domain.repository.DeptRepository;
import com.company.eam.domain.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
public class EmployeeDomainService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DeptRepository deptRepository;

    private static final Random RANDOM = new Random();

    /**
     * Generate an employee number in the format "EMP" + 4 random digits derived from UUID.
     */
    public String generateEmployeeNo() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        int randomDigits = RANDOM.nextInt(10000);
        return String.format("EMP%04d", randomDigits);
    }

    /**
     * Validate that an employee with the given id exists.
     *
     * @throws BusinessException if the employee is not found
     */
    public void validateEmployeeExists(Long id) {
        EmployeeEntity employee = employeeRepository.findById(id);
        if (employee == null) {
            log.warn("EmployeeEntity not found, id={}", id);
            throw new BusinessException("员工不存在");
        }
    }

    /**
     * Validate that the employee number is unique (excluding the given employee id).
     *
     * @throws BusinessException if an employee with the same number already exists
     */
    public void validateEmployeeNoUnique(String no, Long excludeId) {
        if (no == null || no.trim().isEmpty()) {
            log.warn("EmployeeEntity number is empty");
            throw new BusinessException("员工编号不能为空");
        }
        EmployeeEntity existing = employeeRepository.findByEmployeeNo(no.trim());
        if (existing != null && !existing.getId().equals(excludeId)) {
            log.warn("EmployeeEntity number already exists, no={}, excludeId={}", no, excludeId);
            throw new BusinessException("员工编号已存在");
        }
    }

    /**
     * Validate that the department referenced by an employee exists.
     *
     * @throws BusinessException if the department does not exist
     */
    public void validateDeptExistsForEmployee(Long deptId) {
        if (deptId == null) {
            log.warn("EmployeeEntity department id is null");
            throw new BusinessException("部门不能为空");
        }
        DeptEntity dept = deptRepository.findById(deptId);
        if (dept == null) {
            log.warn("Department not found for employee, deptId={}", deptId);
            throw new BusinessException("所属部门不存在");
        }
    }
}
