package com.wave.wavi.log.dto;

import com.wave.wavi.log.model.FailureReason;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class HabitLogResponseDto {
    private Long id;
    private Long habitId;
    private String name;
    private LocalDate date;
    private boolean completed;
    private List<FailureReason> failureReasons;
}
