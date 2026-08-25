package com.company.eam.domain.repository;

import com.company.eam.domain.entity.AttendanceRecord;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRecordRepository {

    AttendanceRecord findByEmployeeIdAndDate(Long empId, LocalDate date);

    List<AttendanceRecord> findByEmployeeId(Long empId);

    List<AttendanceRecord> findByDate(LocalDate date);

    List<AttendanceRecord> findByEmployeeIdAndDateRange(Long empId, LocalDate start, LocalDate end);

    void save(AttendanceRecord r);

    void update(AttendanceRecord r);
}
