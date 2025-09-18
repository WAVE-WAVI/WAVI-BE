package com.wave.wavi.report.dto;

import com.wave.wavi.report.model.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRequestDto {
    private ReportType type;
    private LocalDate startDate;
    private LocalDate endDate;
}
