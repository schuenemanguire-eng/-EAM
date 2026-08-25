package com.company.eam.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.eam.domain.entity.PositionEntity;
import com.company.eam.domain.repository.PositionRepository;
import com.company.eam.infrastructure.persistence.mapper.PositionMapper;
import com.company.eam.infrastructure.persistence.po.PositionPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PositionRepositoryImpl implements PositionRepository {

    @Autowired
    private PositionMapper positionMapper;

    @Override
    public PositionEntity findById(Long id) {
        PositionPO po = positionMapper.selectById(id);
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, PositionEntity.class);
    }

    @Override
    public List<PositionEntity> findAll() {
        List<PositionPO> poList = positionMapper.selectList(null);
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, PositionEntity.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PositionEntity> findByDeptId(Long deptId) {
        List<PositionPO> poList = positionMapper.selectList(
                new LambdaQueryWrapper<PositionPO>().eq(PositionPO::getDeptId, deptId));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, PositionEntity.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(PositionEntity pos) {
        PositionPO po = BeanUtil.copyProperties(pos, PositionPO.class);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        positionMapper.insert(po);
    }

    @Override
    public void update(PositionEntity pos) {
        PositionPO po = BeanUtil.copyProperties(pos, PositionPO.class);
        po.setUpdateTime(LocalDateTime.now());
        positionMapper.updateById(po);
    }

    @Override
    public void deleteById(Long id) {
        positionMapper.deleteById(id);
    }
}
