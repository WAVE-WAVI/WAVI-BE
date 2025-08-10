package com.wave.wavi.log.service;

import com.wave.wavi.habit.model.Habit;
import com.wave.wavi.habit.repository.HabitRepository;
import com.wave.wavi.log.dto.HabitFailureLogRequestDto;
import com.wave.wavi.log.dto.HabitLogResponseDto;
import com.wave.wavi.log.dto.HabitSuccessLogRequestDto;
import com.wave.wavi.log.model.FailureReason;
import com.wave.wavi.log.model.FailureType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                .user(habit.getUser())
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
                .user(habit.getUser())
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

    @Transactional
    public List<FailureReason> getFailureReasons(FailureType type) {
        return failureReasonRepository.findByType(type);
    }

    @Transactional
    public List<HabitLogResponseDto> getLogs(Long habitId, LocalDate date, Boolean completed, Long userId) {
        List<HabitLog> logs = habitLogRepository.findByUserId(userId);
        logs = logs
            .stream()
            .filter(log -> habitId == null || Objects.equals(log.getHabit().getId(), habitId))
            .filter(log -> date == null || log.getDate().equals(date))
            .filter(log -> completed == null || completed == log.isCompleted())
            .toList();

        List<HabitLogResponseDto> habitLogResponseDtos = new ArrayList<>();
        for (HabitLog log : logs) {
            List<HabitFailureLog> failureLogs = habitFailureLogRepository.findByHabitLogId(log.getId());
            List<FailureReason> failureReasons = failureLogs
                    .stream()
                    .map(HabitFailureLog::getReason)
                    .toList();

            HabitLogResponseDto habitLogResponseDto = HabitLogResponseDto.builder()
                    .id(log.getId())
                    .habitId(log.getHabit().getId())
                    .date(log.getDate())
                    .completed(log.isCompleted())
                    .startTime(log.getStartTime())
                    .endTime(log.getEndTime())
                    .failureReasons(failureReasons)
                    .build();

            habitLogResponseDtos.add(habitLogResponseDto);
        }
        return habitLogResponseDtos;
    }

    @Transactional
    public void deleteLog(Long habitId) {
        habitLogRepository.deleteById(habitId);
    }
}
