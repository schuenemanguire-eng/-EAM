package com.company.eam.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.eam.domain.entity.UserEntity;
import com.company.eam.domain.repository.UserRepository;
import com.company.eam.infrastructure.persistence.mapper.SysUserMapper;
import com.company.eam.infrastructure.persistence.po.SysUserPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserEntity findById(Long id) {
        SysUserPO po = sysUserMapper.selectById(id);
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, UserEntity.class);
    }

    @Override
    public UserEntity findByUsername(String username) {
        SysUserPO po = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUserPO>().eq(SysUserPO::getUsername, username));
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, UserEntity.class);
    }

    @Override
    public UserEntity findByEmployeeId(Long empId) {
        SysUserPO po = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUserPO>().eq(SysUserPO::getEmployeeId, empId));
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, UserEntity.class);
    }

    @Override
    public List<UserEntity> findAll() {
        List<SysUserPO> poList = sysUserMapper.selectList(null);
        if (poList == null || poList.isEmpty()) {
            return List.of();
        }
        return poList.stream()
                .map(po -> BeanUtil.copyProperties(po, UserEntity.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(UserEntity u) {
        SysUserPO po = BeanUtil.copyProperties(u, SysUserPO.class);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(po);
    }

    @Override
    public void update(UserEntity u) {
        SysUserPO po = BeanUtil.copyProperties(u, SysUserPO.class);
        po.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(po);
    }

    @Override
    public void deleteById(Long id) {
        sysUserMapper.deleteById(id);
    }
}
