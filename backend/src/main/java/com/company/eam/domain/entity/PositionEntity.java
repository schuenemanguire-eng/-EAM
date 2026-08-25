package com.company.eam.domain.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PositionEntity {
    private Long id;
    private String name;
    private Long deptId;
    private String rank;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
