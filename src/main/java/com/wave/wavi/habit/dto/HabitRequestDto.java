package com.wave.wavi.habit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HabitRequestDto {
    private String name;
    private List<Integer> dayOfWeek;
    private Long icon;
    private Long aim;
    private Time startTime;
    private Time endTime;
}
