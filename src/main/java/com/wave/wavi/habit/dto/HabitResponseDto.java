package com.wave.wavi.habit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        private String icon;
        private List<Integer> dayOfWeek;
        private StatusType status;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        private LocalTime startTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        private LocalTime endTime;
}
