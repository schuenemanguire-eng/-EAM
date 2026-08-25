package com.company.eam.application.service;

import cn.hutool.core.bean.BeanUtil;
import com.company.eam.api.dto.request.PositionRequest;
import com.company.eam.api.dto.response.PositionVO;
import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.DeptEntity;
import com.company.eam.domain.entity.PositionEntity;
import com.company.eam.domain.repository.DeptRepository;
import com.company.eam.domain.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionAppService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DeptRepository deptRepository;

    public List<PositionVO> listPositions() {
        List<PositionEntity> list = positionRepository.findAll();
        return list.stream().map(p -> {
            PositionVO vo = BeanUtil.copyProperties(p, PositionVO.class);
            if (p.getDeptId() != null) {
                DeptEntity d = deptRepository.findById(p.getDeptId());
                if (d != null) vo.setDeptName(d.getName());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    public void createPosition(PositionRequest req) {
        PositionEntity pos = BeanUtil.copyProperties(req, PositionEntity.class);
        positionRepository.save(pos);
    }

    public void updatePosition(PositionRequest req) {
        PositionEntity pos = positionRepository.findById(req.getId());
        if (pos == null) throw new BusinessException("职位不存在");
        BeanUtil.copyProperties(req, pos);
        positionRepository.update(pos);
    }

    public void deletePosition(Long id) {
        positionRepository.deleteById(id);
    }
}
