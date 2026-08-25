package com.company.eam.domain.repository;

import com.company.eam.domain.entity.PositionEntity;
import java.util.List;

public interface PositionRepository {

    PositionEntity findById(Long id);

    List<PositionEntity> findAll();

    List<PositionEntity> findByDeptId(Long deptId);

    void save(PositionEntity pos);

    void update(PositionEntity pos);

    void deleteById(Long id);
}
