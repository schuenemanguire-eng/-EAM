package com.company.eam.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.eam.domain.entity.LeaveApplication;
import com.company.eam.domain.repository.LeaveApplicationRepository;
import com.company.eam.infrastructure.persistence.mapper.LeaveApplicationMapper;
import com.company.eam.infrastructure.persistence.po.LeaveApplicationPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LeaveApplicationRepositoryImpl implements LeaveApplicationRepository {

    @Autowired
    private LeaveApplicationMapper leaveApplicationMapper;

    @Override
    public LeaveApplication findById(Long id) {
        LeaveApplicationPO po = leaveApplicationMapper.selectById(id);
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, LeaveApplication.class);
    }

    @Override
    public List<LeaveApplication> findByEmployeeId(Long empId) {
        List<LeaveApplicationPO> poList = leaveApplicationMapper.selectList(
                new LambdaQueryWrapper<LeaveApplicationPO>().eq(LeaveApplicationPO::getEmployeeId, empId));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, LeaveApplication.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplication> findByApproverId(Long approverId) {
        List<LeaveApplicationPO> poList = leaveApplicationMapper.selectList(
                new LambdaQueryWrapper<LeaveApplicationPO>().eq(LeaveApplicationPO::getApproverId, approverId));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, LeaveApplication.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplication> findByStatus(String status) {
        List<LeaveApplicationPO> poList = leaveApplicationMapper.selectList(
                new LambdaQueryWrapper<LeaveApplicationPO>().eq(LeaveApplicationPO::getStatus, status));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, LeaveApplication.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplication> findAll() {
        List<LeaveApplicationPO> poList = leaveApplicationMapper.selectList(null);
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, LeaveApplication.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(LeaveApplication l) {
        LeaveApplicationPO po = BeanUtil.copyProperties(l, LeaveApplicationPO.class);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        leaveApplicationMapper.insert(po);
    }

    @Override
    public void update(LeaveApplication l) {
        LeaveApplicationPO po = BeanUtil.copyProperties(l, LeaveApplicationPO.class);
        po.setUpdateTime(LocalDateTime.now());
        leaveApplicationMapper.updateById(po);
    }
}
