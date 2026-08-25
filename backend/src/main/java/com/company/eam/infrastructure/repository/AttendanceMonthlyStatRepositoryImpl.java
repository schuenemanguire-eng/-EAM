package com.company.eam.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.eam.domain.entity.AttendanceMonthlyStat;
import com.company.eam.domain.repository.AttendanceMonthlyStatRepository;
import com.company.eam.infrastructure.persistence.mapper.AttendanceMonthlyStatMapper;
import com.company.eam.infrastructure.persistence.po.AttendanceMonthlyStatPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AttendanceMonthlyStatRepositoryImpl implements AttendanceMonthlyStatRepository {

    @Autowired
    private AttendanceMonthlyStatMapper attendanceMonthlyStatMapper;

    @Override
    public Optional<AttendanceMonthlyStat> findByEmployeeIdAndYearMonth(Long empId, Integer year, Integer month) {
        AttendanceMonthlyStatPO po = attendanceMonthlyStatMapper.selectOne(
                new LambdaQueryWrapper<AttendanceMonthlyStatPO>()
                        .eq(AttendanceMonthlyStatPO::getEmployeeId, empId)
                        .eq(AttendanceMonthlyStatPO::getYear, year)
                        .eq(AttendanceMonthlyStatPO::getMonth, month));
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(BeanUtil.copyProperties(po, AttendanceMonthlyStat.class));
    }

    @Override
    public List<AttendanceMonthlyStat> findByEmployeeId(Long empId) {
        List<AttendanceMonthlyStatPO> poList = attendanceMonthlyStatMapper.selectList(
                new LambdaQueryWrapper<AttendanceMonthlyStatPO>().eq(AttendanceMonthlyStatPO::getEmployeeId, empId));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, AttendanceMonthlyStat.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceMonthlyStat> findAll() {
        List<AttendanceMonthlyStatPO> poList = attendanceMonthlyStatMapper.selectList(null);
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, AttendanceMonthlyStat.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(AttendanceMonthlyStat s) {
        AttendanceMonthlyStatPO po = BeanUtil.copyProperties(s, AttendanceMonthlyStatPO.class);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        attendanceMonthlyStatMapper.insert(po);
    }

    @Override
    public void update(AttendanceMonthlyStat s) {
        AttendanceMonthlyStatPO po = BeanUtil.copyProperties(s, AttendanceMonthlyStatPO.class);
        po.setUpdateTime(LocalDateTime.now());
        attendanceMonthlyStatMapper.updateById(po);
    }
}
