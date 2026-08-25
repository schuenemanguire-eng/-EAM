package com.company.eam.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptVO {

    private Long id;

    private String name;

    private Long parentId;

    private String parentName;

    private String managerName;

    private Integer sort;

    private Integer status;

    private List<DeptVO> children;
}
