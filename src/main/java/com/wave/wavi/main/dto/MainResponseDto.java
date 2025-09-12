package com.wave.wavi.main.dto;

import com.wave.wavi.habit.dto.HabitResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MainResponseDto {
    private String nickname;
    private Long profileImage;
    private List<HabitResponseDto> todayHabits;
}
