package com.company.eam.domain.repository;

import com.company.eam.domain.entity.AttendanceMonthlyStat;
import java.util.List;
import java.util.Optional;

public interface AttendanceMonthlyStatRepository {

    Optional<AttendanceMonthlyStat> findByEmployeeIdAndYearMonth(Long empId, Integer year, Integer month);

    List<AttendanceMonthlyStat> findByEmployeeId(Long empId);

    List<AttendanceMonthlyStat> findAll();

    void save(AttendanceMonthlyStat s);

    void update(AttendanceMonthlyStat s);
}
