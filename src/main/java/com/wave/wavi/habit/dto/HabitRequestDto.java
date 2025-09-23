package com.wave.wavi.habit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HabitRequestDto {
    private String name;
    private List<Integer> dayOfWeek;
    private String icon;
    @Schema(type = "string")
    private LocalTime startTime;
    @Schema(type = "string")
    private LocalTime endTime;
}
