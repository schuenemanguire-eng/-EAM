package com.company.eam.application.service;

import cn.hutool.core.bean.BeanUtil;
import com.company.eam.api.dto.request.EmployeeRequest;
import com.company.eam.api.dto.response.EmployeeVO;
import com.company.eam.common.BusinessException;
import com.company.eam.domain.entity.DeptEntity;
import com.company.eam.domain.entity.EmployeeEntity;
import com.company.eam.domain.entity.PositionEntity;
import com.company.eam.domain.repository.DeptRepository;
import com.company.eam.domain.repository.EmployeeRepository;
import com.company.eam.domain.repository.PositionRepository;
import com.company.eam.domain.service.EmployeeDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeAppService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EmployeeDomainService employeeDomainService;

    public List<EmployeeVO> listEmployees(String keyword, Long deptId) {
        List<EmployeeEntity> list = employeeRepository.findAll();
        return list.stream()
                .filter(e -> e.getStatus() != null && e.getStatus() == 1)
                .filter(e -> deptId == null || deptId.equals(e.getDeptId()))
                .filter(e -> keyword == null || keyword.isEmpty() ||
                        e.getName().contains(keyword) || e.getEmployeeNo().contains(keyword))
                .map(e -> toVO(e))
                .collect(Collectors.toList());
    }

    public EmployeeVO getEmployee(Long id) {
        EmployeeEntity e = employeeRepository.findById(id);
        if (e == null) throw new BusinessException("员工不存在");
        return toVO(e);
    }

    private EmployeeVO toVO(EmployeeEntity e) {
        EmployeeVO vo = BeanUtil.copyProperties(e, EmployeeVO.class);
        if (e.getDeptId() != null) {
            DeptEntity d = deptRepository.findById(e.getDeptId());
            if (d != null) vo.setDeptName(d.getName());
        }
        if (e.getPositionId() != null) {
            PositionEntity p = positionRepository.findById(e.getPositionId());
            if (p != null) vo.setPositionName(p.getName());
        }
        return vo;
    }

    public void createEmployee(EmployeeRequest req) {
        if (req.getEmployeeNo() == null || req.getEmployeeNo().isEmpty()) {
            req.setEmployeeNo(employeeDomainService.generateEmployeeNo());
        }
        employeeDomainService.validateEmployeeNoUnique(req.getEmployeeNo(), null);
        employeeDomainService.validateDeptExistsForEmployee(req.getDeptId());
        EmployeeEntity emp = BeanUtil.copyProperties(req, EmployeeEntity.class);
        employeeRepository.save(emp);
    }

    public void updateEmployee(EmployeeRequest req) {
        EmployeeEntity emp = employeeRepository.findById(req.getId());
        if (emp == null) throw new BusinessException("员工不存在");
        if (req.getEmployeeNo() != null) {
            employeeDomainService.validateEmployeeNoUnique(req.getEmployeeNo(), req.getId());
        }
        BeanUtil.copyProperties(req, emp);
        employeeRepository.update(emp);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public void quit(Long id) {
        EmployeeEntity emp = employeeRepository.findById(id);
        if (emp == null) throw new BusinessException("员工不存在");
        emp.setStatus(0);
        employeeRepository.update(emp);
    }
}
