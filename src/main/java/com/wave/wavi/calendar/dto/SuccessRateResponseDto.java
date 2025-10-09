package com.wave.wavi.calendar.dto;

import lombok.Getter;

@Getter
public class SuccessRateResponseDto {
    private final int successRate;
    private final int totalCount;
    private final int successCount;
    public SuccessRateResponseDto(int successRate, int totalCount, int successCount) {
        this.successRate = successRate;
        this.totalCount = totalCount;
        this.successCount = successCount;
    }
}
