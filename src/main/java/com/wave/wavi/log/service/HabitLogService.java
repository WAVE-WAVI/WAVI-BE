package com.wave.wavi.log.service;

import com.wave.wavi.habit.model.Habit;
import com.wave.wavi.habit.repository.HabitRepository;
import com.wave.wavi.log.dto.HabitFailureLogRequestDto;
import com.wave.wavi.log.dto.HabitSuccessLogRequestDto;
import com.wave.wavi.log.model.FailureReason;
import com.wave.wavi.log.model.HabitFailureLog;
import com.wave.wavi.log.model.HabitLog;
import com.wave.wavi.log.repository.FailureReasonRepository;
import com.wave.wavi.log.repository.HabitFailureLogRepository;
import com.wave.wavi.log.repository.HabitLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;
    private final HabitFailureLogRepository habitFailureLogRepository;
    private final FailureReasonRepository failureReasonRepository;

    @Transactional
    public void saveSuccess(@RequestBody HabitSuccessLogRequestDto requestDto) {
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

    @Transactional
    public void saveFailure(@RequestBody HabitFailureLogRequestDto requestDto) {
        Habit habit = habitRepository.findById(requestDto.getHabitId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 습관을 찾지 못했습니다."));
        HabitLog habitLog = HabitLog.builder()
                .habit(habit)
                .date(LocalDate.now())
                .completed(false)
                .build();
        habitLogRepository.save(habitLog);

        List<Long> failureReasonIds = requestDto.getFailureReasonIds();
        for (Long failureReasonId : failureReasonIds) {
            FailureReason failureReason = failureReasonRepository.findById(failureReasonId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 이유를 찾지 못했습니다."));
            HabitFailureLog habitFailureLog = HabitFailureLog.builder()
                    .habitLog(habitLog)
                    .reason(failureReason)
                    .build();
            habitFailureLogRepository.save(habitFailureLog);
        }
    }
}
