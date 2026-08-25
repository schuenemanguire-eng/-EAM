package com.company.eam.application.service;

import cn.hutool.core.bean.BeanUtil;
import com.company.eam.api.dto.request.DeptRequest;
import com.company.eam.api.dto.response.DeptVO;
import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.DeptEntity;
import com.company.eam.domain.repository.DeptRepository;
import com.company.eam.domain.service.DeptDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeptAppService {

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private DeptDomainService deptDomainService;

    public List<DeptVO> getDeptTree() {
        List<DeptEntity> all = deptRepository.findAll();
        Map<Long, List<DeptEntity>> map = all.stream().collect(Collectors.groupingBy(DeptEntity::getParentId));
        return buildTree(map, 0L);
    }

    private List<DeptVO> buildTree(Map<Long, List<DeptEntity>> map, Long parentId) {
        List<DeptEntity> children = map.getOrDefault(parentId, Collections.emptyList());
        return children.stream().map(d -> {
            DeptVO vo = BeanUtil.copyProperties(d, DeptVO.class);
            vo.setChildren(buildTree(map, d.getId()));
            return vo;
        }).collect(Collectors.toList());
    }

    public DeptVO getDept(Long id) {
        DeptEntity d = deptRepository.findById(id);
        if (d == null) throw new BusinessException("部门不存在");
        return BeanUtil.copyProperties(d, DeptVO.class);
    }

    public void createDept(DeptRequest req) {
        deptDomainService.validateDeptNameUnique(req.getName(), null);
        DeptEntity dept = BeanUtil.copyProperties(req, DeptEntity.class);
        deptRepository.save(dept);
    }

    public void updateDept(DeptRequest req) {
        DeptEntity existing = deptRepository.findById(req.getId());
        if (existing == null) throw new BusinessException("部门不存在");
        deptDomainService.validateDeptNameUnique(req.getName(), req.getId());
        BeanUtil.copyProperties(req, existing);
        deptRepository.update(existing);
    }

    public void deleteDept(Long id) {
        deptDomainService.validateDeptCanBeDeleted(id);
        deptRepository.deleteById(id);
    }
}
