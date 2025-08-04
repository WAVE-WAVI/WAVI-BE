package com.wave.wavi.habit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HabitSaveRequestDto {
    private String name;
    private List<Integer> dayOfWeek;
    private Long icon;
}
