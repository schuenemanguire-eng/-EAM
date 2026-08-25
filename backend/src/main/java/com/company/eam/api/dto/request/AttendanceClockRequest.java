package com.company.eam.api.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceClockRequest {

    @NotBlank
    private String clockType;

    @Builder.Default
    private LocalDate date = LocalDate.now();
}
