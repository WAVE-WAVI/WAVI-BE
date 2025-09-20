package com.wave.wavi.habit.dto;

import com.wave.wavi.habit.model.StatusType;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(type = "string")
        private LocalTime startTime;
        @Schema(type = "string")
        private LocalTime endTime;
}
