package com.company.eam.api.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptRequest {

    private Long id;

    @NotBlank
    private String name;

    @Builder.Default
    private Long parentId = 0L;

    private Long managerId;

    private Integer sort;

    private Integer status;
}
