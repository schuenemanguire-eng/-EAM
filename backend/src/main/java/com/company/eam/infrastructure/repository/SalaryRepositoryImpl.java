package com.company.eam.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.eam.domain.entity.Salary;
import com.company.eam.domain.repository.SalaryRepository;
import com.company.eam.infrastructure.persistence.mapper.SalaryMapper;
import com.company.eam.infrastructure.persistence.po.SalaryPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SalaryRepositoryImpl implements SalaryRepository {

    @Autowired
    private SalaryMapper salaryMapper;

    @Override
    public Optional<Salary> findByEmployeeIdAndYearMonth(Long empId, Integer year, Integer month) {
        SalaryPO po = salaryMapper.selectOne(
                new LambdaQueryWrapper<SalaryPO>()
                        .eq(SalaryPO::getEmployeeId, empId)
                        .eq(SalaryPO::getYear, year)
                        .eq(SalaryPO::getMonth, month));
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(BeanUtil.copyProperties(po, Salary.class));
    }

    @Override
    public List<Salary> findByEmployeeId(Long empId) {
        List<SalaryPO> poList = salaryMapper.selectList(
                new LambdaQueryWrapper<SalaryPO>().eq(SalaryPO::getEmployeeId, empId));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, Salary.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Salary> findByYearMonth(Integer year, Integer month) {
        List<SalaryPO> poList = salaryMapper.selectList(
                new LambdaQueryWrapper<SalaryPO>()
                        .eq(SalaryPO::getYear, year)
                        .eq(SalaryPO::getMonth, month));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, Salary.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Salary> findAll() {
        List<SalaryPO> poList = salaryMapper.selectList(null);
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, Salary.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Salary s) {
        SalaryPO po = BeanUtil.copyProperties(s, SalaryPO.class);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        salaryMapper.insert(po);
    }

    @Override
    public void update(Salary s) {
        SalaryPO po = BeanUtil.copyProperties(s, SalaryPO.class);
        po.setUpdateTime(LocalDateTime.now());
        salaryMapper.updateById(po);
    }
}
