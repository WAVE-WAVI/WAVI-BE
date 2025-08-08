package com.wave.wavi.log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HabitLogRequestDto {
    private Long habitId;
    private Time startTime;
    private Time endTime;
}
