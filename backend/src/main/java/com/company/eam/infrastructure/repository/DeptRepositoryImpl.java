package com.company.eam.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.eam.domain.entity.DeptEntity;
import com.company.eam.domain.repository.DeptRepository;
import com.company.eam.infrastructure.persistence.mapper.DeptMapper;
import com.company.eam.infrastructure.persistence.po.DeptPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DeptRepositoryImpl implements DeptRepository {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public DeptEntity findById(Long id) {
        DeptPO po = deptMapper.selectById(id);
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, DeptEntity.class);
    }

    @Override
    public List<DeptEntity> findAll() {
        List<DeptPO> poList = deptMapper.selectList(null);
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, DeptEntity.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DeptEntity> findByParentId(Long parentId) {
        List<DeptPO> poList = deptMapper.selectList(
                new LambdaQueryWrapper<DeptPO>().eq(DeptPO::getParentId, parentId));
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, DeptEntity.class))
                .collect(Collectors.toList());
    }

    @Override
    public DeptEntity findByName(String name) {
        DeptPO po = deptMapper.selectOne(
                new LambdaQueryWrapper<DeptPO>().eq(DeptPO::getName, name));
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, DeptEntity.class);
    }

    @Override
    public void save(DeptEntity dept) {
        DeptPO po = BeanUtil.copyProperties(dept, DeptPO.class);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        deptMapper.insert(po);
    }

    @Override
    public void update(DeptEntity dept) {
        DeptPO po = BeanUtil.copyProperties(dept, DeptPO.class);
        po.setUpdateTime(LocalDateTime.now());
        deptMapper.updateById(po);
    }

    @Override
    public void deleteById(Long id) {
        deptMapper.deleteById(id);
    }
}
