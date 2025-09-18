package com.wave.wavi.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopFailureReasonResponseDto {
    @JsonProperty("habit_id")
    private Long habitId;

    private String habit;

    private List<String> reasons;
}
