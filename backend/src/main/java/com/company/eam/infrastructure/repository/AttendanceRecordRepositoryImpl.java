package com.company.eam.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.eam.domain.entity.AttendanceRecord;
import com.company.eam.domain.repository.AttendanceRecordRepository;
import com.company.eam.infrastructure.persistence.mapper.AttendanceRecordMapper;
import com.company.eam.infrastructure.persistence.po.AttendanceRecordPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AttendanceRecordRepositoryImpl implements AttendanceRecordRepository {

    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Override
    public AttendanceRecord findByEmployeeIdAndDate(Long empId, LocalDate date) {
        AttendanceRecordPO po = attendanceRecordMapper.selectOne(
                new LambdaQueryWrapper<AttendanceRecordPO>()
                        .eq(AttendanceRecordPO::getEmployeeId, empId)
                        .eq(AttendanceRecordPO::getDate, date));
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, AttendanceRecord.class);
    }

    @Override
    public List<AttendanceRecord> findByEmployeeId(Long empId) {
        List<AttendanceRecordPO> poList = attendanceRecordMapper.selectList(
                new LambdaQueryWrapper<AttendanceRecordPO>().eq(AttendanceRecordPO::getEmployeeId, empId));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, AttendanceRecord.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecord> findByDate(LocalDate date) {
        List<AttendanceRecordPO> poList = attendanceRecordMapper.selectList(
                new LambdaQueryWrapper<AttendanceRecordPO>().eq(AttendanceRecordPO::getDate, date));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, AttendanceRecord.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecord> findByEmployeeIdAndDateRange(Long empId, LocalDate start, LocalDate end) {
        List<AttendanceRecordPO> poList = attendanceRecordMapper.selectList(
                new LambdaQueryWrapper<AttendanceRecordPO>()
                        .eq(AttendanceRecordPO::getEmployeeId, empId)
                        .between(AttendanceRecordPO::getDate, start, end));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, AttendanceRecord.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(AttendanceRecord r) {
        AttendanceRecordPO po = BeanUtil.copyProperties(r, AttendanceRecordPO.class);
        po.setCreateTime(LocalDateTime.now());
        attendanceRecordMapper.insert(po);
    }

    @Override
    public void update(AttendanceRecord r) {
        AttendanceRecordPO po = BeanUtil.copyProperties(r, AttendanceRecordPO.class);
        attendanceRecordMapper.updateById(po);
    }
}
