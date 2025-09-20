package com.wave.wavi.habit.dto;

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
    private LocalTime startTime;
    private LocalTime endTime;
}
