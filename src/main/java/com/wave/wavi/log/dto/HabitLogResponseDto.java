package com.wave.wavi.log.dto;

import com.wave.wavi.log.model.FailureReason;
import lombok.Builder;
import lombok.Getter;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class HabitLogResponseDto {
    private Long id;
    private Long habitId;
    private LocalDate date;
    private boolean completed;
    private Time startTime;
    private Time endTime;
    private List<FailureReason> failureReasons;
}
