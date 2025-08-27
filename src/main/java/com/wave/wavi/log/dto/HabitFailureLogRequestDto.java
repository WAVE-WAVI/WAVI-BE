package com.wave.wavi.log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HabitFailureLogRequestDto {
    private List<Long> failureReasonIds;
    private String customReason;
}
