package com.company.eam.domain.repository;

import com.company.eam.domain.entity.Salary;
import java.util.List;
import java.util.Optional;

public interface SalaryRepository {

    Optional<Salary> findByEmployeeIdAndYearMonth(Long empId, Integer year, Integer month);

    List<Salary> findByEmployeeId(Long empId);

    List<Salary> findByYearMonth(Integer year, Integer month);

    List<Salary> findAll();

    void save(Salary s);

    void update(Salary s);
}
