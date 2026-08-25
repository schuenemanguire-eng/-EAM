package com.company.eam.domain.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeptEntity {
    private Long id;
    private String name;
    private Long parentId;
    private Long managerId;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
