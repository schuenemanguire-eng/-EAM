package com.company.eam.domain.service;

import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.DeptEntity;
import com.company.eam.domain.entity.EmployeeEntity;
import com.company.eam.domain.repository.DeptRepository;
import com.company.eam.domain.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class DeptDomainService {

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Validate that a department with the given id exists.
     *
     * @throws BusinessException if the department is not found
     */
    public void validateDeptExists(Long id) {
        DeptEntity dept = deptRepository.findById(id);
        if (dept == null) {
            log.warn("Department not found, id={}", id);
            throw new BusinessException("部门不存在");
        }
    }

    /**
     * Validate that the department name is unique (excluding the given dept id).
     *
     * @throws BusinessException if a department with the same name already exists
     */
    public void validateDeptNameUnique(String name, Long excludeId) {
        if (name == null || name.trim().isEmpty()) {
            log.warn("Department name is empty");
            throw new BusinessException("部门名称不能为空");
        }
        DeptEntity existing = deptRepository.findByName(name.trim());
        if (existing != null && !existing.getId().equals(excludeId)) {
            log.warn("Department name already exists, name={}, excludeId={}", name, excludeId);
            throw new BusinessException("部门名称已存在");
        }
    }

    /**
     * Validate that a department can be deleted (no children and no employees).
     *
     * @throws BusinessException if the department has child departments or employees
     */
    public void validateDeptCanBeDeleted(Long id) {
        validateDeptExists(id);

        List<DeptEntity> children = deptRepository.findByParentId(id);
        if (children != null && !children.isEmpty()) {
            log.warn("Department has child departments, cannot delete, id={}", id);
            throw new BusinessException("该部门下存在子部门，无法删除");
        }

        List<EmployeeEntity> employees = employeeRepository.findByDeptId(id);
        if (employees != null && !employees.isEmpty()) {
            log.warn("Department has employees, cannot delete, id={}", id);
            throw new BusinessException("该部门下存在员工，无法删除");
        }
    }
}
