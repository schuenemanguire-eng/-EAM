package com.company.eam.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.eam.domain.entity.EmployeeEntity;
import com.company.eam.domain.repository.EmployeeRepository;
import com.company.eam.infrastructure.persistence.mapper.EmployeeMapper;
import com.company.eam.infrastructure.persistence.po.EmployeePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public EmployeeEntity findById(Long id) {
        EmployeePO po = employeeMapper.selectById(id);
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, EmployeeEntity.class);
    }

    @Override
    public EmployeeEntity findByEmployeeNo(String no) {
        EmployeePO po = employeeMapper.selectOne(
                new LambdaQueryWrapper<EmployeePO>().eq(EmployeePO::getEmployeeNo, no));
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, EmployeeEntity.class);
    }

    @Override
    public List<EmployeeEntity> findAll() {
        List<EmployeePO> poList = employeeMapper.selectList(null);
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, EmployeeEntity.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeEntity> findByDeptId(Long deptId) {
        List<EmployeePO> poList = employeeMapper.selectList(
                new LambdaQueryWrapper<EmployeePO>().eq(EmployeePO::getDeptId, deptId));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, EmployeeEntity.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeEntity> findByStatus(Integer status) {
        List<EmployeePO> poList = employeeMapper.selectList(
                new LambdaQueryWrapper<EmployeePO>().eq(EmployeePO::getStatus, status));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, EmployeeEntity.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(EmployeeEntity emp) {
        EmployeePO po = BeanUtil.copyProperties(emp, EmployeePO.class);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        employeeMapper.insert(po);
    }

    @Override
    public void update(EmployeeEntity emp) {
        EmployeePO po = BeanUtil.copyProperties(emp, EmployeePO.class);
        po.setUpdateTime(LocalDateTime.now());
        employeeMapper.updateById(po);
    }

    @Override
    public void deleteById(Long id) {
        employeeMapper.deleteById(id);
    }
}
