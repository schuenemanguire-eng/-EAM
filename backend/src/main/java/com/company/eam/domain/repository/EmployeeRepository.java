package com.company.eam.domain.repository;

import com.company.eam.domain.entity.EmployeeEntity;
import java.util.List;

public interface EmployeeRepository {

    EmployeeEntity findById(Long id);

    EmployeeEntity findByEmployeeNo(String no);

    List<EmployeeEntity> findAll();

    List<EmployeeEntity> findByDeptId(Long deptId);

    List<EmployeeEntity> findByStatus(Integer status);

    void save(EmployeeEntity emp);

    void update(EmployeeEntity emp);

    void deleteById(Long id);
}
