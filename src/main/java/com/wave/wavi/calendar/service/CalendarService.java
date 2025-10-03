package com.wave.wavi.calendar.service;

import com.wave.wavi.calendar.dto.SuccessRateResponseDto;
import com.wave.wavi.log.service.HabitLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {
    private final HabitLogService habitLogService;

    @Transactional
    public SuccessRateResponseDto getSuccessRate(LocalDate startDate, LocalDate endDate, String email) {
        int totalCount = habitLogService.getLogs(null, startDate, endDate, null, email).size();
        int successCount = habitLogService.getLogs(null, startDate, endDate, true, email).size();
        int successRate = (totalCount == 0) ? 0 : successCount * 100 / totalCount;
        return new SuccessRateResponseDto(successRate, totalCount, successCount);
    }
}
