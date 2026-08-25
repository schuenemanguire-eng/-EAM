package com.company.eam.domain.repository;

import com.company.eam.domain.entity.LeaveApplication;
import java.util.List;

public interface LeaveApplicationRepository {

    LeaveApplication findById(Long id);

    List<LeaveApplication> findByEmployeeId(Long empId);

    List<LeaveApplication> findByApproverId(Long approverId);

    List<LeaveApplication> findByStatus(String status);

    List<LeaveApplication> findAll();

    void save(LeaveApplication l);

    void update(LeaveApplication l);
}
