package com.company.eam.domain.repository;

import com.company.eam.domain.entity.DeptEntity;
import java.util.List;

public interface DeptRepository {

    DeptEntity findById(Long id);

    List<DeptEntity> findAll();

    List<DeptEntity> findByParentId(Long parentId);

    DeptEntity findByName(String name);

    void save(DeptEntity dept);

    void update(DeptEntity dept);

    void deleteById(Long id);
}
