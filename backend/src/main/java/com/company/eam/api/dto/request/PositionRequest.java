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
public class PositionRequest {

    private Long id;

    @NotBlank
    private String name;

    private Long deptId;

    private String rank;
}
