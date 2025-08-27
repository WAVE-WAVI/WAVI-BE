package com.wave.wavi.habit.dto;

import com.wave.wavi.habit.model.StatusType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class HabitResponseDto {
        private Long id;
        private String name;
        private Long icon;
        private List<Integer> dayOfWeek;
        private StatusType status;
        private LocalTime startTime;
        private LocalTime endTime;
}
