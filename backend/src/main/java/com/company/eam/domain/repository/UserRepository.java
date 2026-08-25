package com.company.eam.domain.repository;

import com.company.eam.domain.entity.UserEntity;
import java.util.List;

public interface UserRepository {

    UserEntity findById(Long id);

    UserEntity findByUsername(String username);

    UserEntity findByEmployeeId(Long empId);

    List<UserEntity> findAll();

    void save(UserEntity u);

    void update(UserEntity u);

    void deleteById(Long id);
}
