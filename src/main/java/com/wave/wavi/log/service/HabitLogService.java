package com.wave.wavi.log.service;

import com.wave.wavi.habit.model.Habit;
import com.wave.wavi.habit.repository.HabitRepository;
import com.wave.wavi.log.dto.HabitLogRequestDto;
import com.wave.wavi.log.model.HabitLog;
import com.wave.wavi.log.repository.HabitLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;

    @Transactional
    public void saveSuccess(@RequestBody HabitLogRequestDto requestDto) {
        Habit habit = habitRepository.findById(requestDto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 습관을 찾지 못했습니다."));
        HabitLog habitLog = HabitLog.builder()
                    .habit(habit)
                    .date(LocalDate.now())
                    .completed(true)
                    .startTime(requestDto.getStartTime())
                    .endTime(requestDto.getEndTime())
                    .build();
        habitLogRepository.save(habitLog);
    }
}
