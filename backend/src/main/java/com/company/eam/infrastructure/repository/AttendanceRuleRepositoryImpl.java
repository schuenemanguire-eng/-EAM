package com.company.eam.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.company.eam.domain.entity.AttendanceRule;
import com.company.eam.domain.repository.AttendanceRuleRepository;
import com.company.eam.infrastructure.persistence.mapper.AttendanceRuleMapper;
import com.company.eam.infrastructure.persistence.po.AttendanceRulePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class AttendanceRuleRepositoryImpl implements AttendanceRuleRepository {

    @Autowired
    private AttendanceRuleMapper attendanceRuleMapper;

    @Override
    public Optional<AttendanceRule> findByPrimaryKey() {
        AttendanceRulePO po = attendanceRuleMapper.selectById(1L);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(BeanUtil.copyProperties(po, AttendanceRule.class));
    }

    @Override
    public void save(AttendanceRule rule) {
        AttendanceRulePO po = BeanUtil.copyProperties(rule, AttendanceRulePO.class);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        attendanceRuleMapper.insert(po);
    }

    @Override
    public void update(AttendanceRule rule) {
        AttendanceRulePO po = BeanUtil.copyProperties(rule, AttendanceRulePO.class);
        po.setUpdateTime(LocalDateTime.now());
        attendanceRuleMapper.updateById(po);
    }
}
