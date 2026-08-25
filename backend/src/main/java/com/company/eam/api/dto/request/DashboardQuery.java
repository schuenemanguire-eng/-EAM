package com.company.eam.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardQuery {

    @Builder.Default
    private Integer year = LocalDate.now().getYear();

    @Builder.Default
    private Integer month = LocalDate.now().getMonthValue();
}
