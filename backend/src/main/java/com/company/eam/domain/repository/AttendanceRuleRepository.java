package com.company.eam.domain.repository;

import com.company.eam.domain.entity.AttendanceRule;
import java.util.Optional;

public interface AttendanceRuleRepository {

    Optional<AttendanceRule> findByPrimaryKey();

    void save(AttendanceRule rule);

    void update(AttendanceRule rule);
}
