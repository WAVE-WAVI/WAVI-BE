package com.wave.wavi.log.service;

import com.wave.wavi.habit.model.Habit;
import com.wave.wavi.habit.model.StatusType;
import com.wave.wavi.habit.repository.HabitRepository;
import com.wave.wavi.log.dto.HabitFailureLogRequestDto;
import com.wave.wavi.log.dto.HabitLogResponseDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;
    private final HabitFailureLogRepository habitFailureLogRepository;
    private final FailureReasonRepository failureReasonRepository;

    @Transactional
    public void saveSuccess(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 습관을 찾지 못했습니다."));
        habit.setStatus(StatusType.COMPLETED);
        HabitLog habitLog = HabitLog.builder()
                .habit(habit)
                .user(habit.getUser())
                .date(LocalDate.now())
                .completed(true)
                .build();
        habitLogRepository.save(habitLog);
    }

    @Transactional
    public void saveFailure(Long habitId, @RequestBody HabitFailureLogRequestDto requestDto) {
        Habit habit = habitRepository.findById(habitId)
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

        if (requestDto.getCustomReason() != null) {
            HabitFailureLog habitFailureLog = HabitFailureLog.builder()
                    .habitLog(habitLog)
                    .reason(null)
                    .customReason(requestDto.getCustomReason())
                    .build();
            habitFailureLogRepository.save(habitFailureLog);
        }
    }

    @Transactional
    public List<FailureReason> getFailureReasons() {
        return failureReasonRepository.findAll();
    }

    @Transactional
    public List<HabitLogResponseDto> getLogs(Long habitId, LocalDate startDate, LocalDate endDate, Boolean completed, Long userId) {
        List<HabitLog> logs = habitLogRepository.findByUserId(userId);
        logs = logs
            .stream()
            .filter(log -> habitId == null || Objects.equals(log.getHabit().getId(), habitId))
            .filter(log -> startDate == null || log.getDate().isEqual(startDate) || log.getDate().isAfter(startDate))
            .filter(log -> endDate == null || log.getDate().isEqual(endDate) || log.getDate().isBefore(endDate))
            .filter(log -> completed == null || completed == log.isCompleted())
            .toList();

        List<HabitLogResponseDto> habitLogResponseDtos = new ArrayList<>();
        for (HabitLog log : logs) {
            List<HabitFailureLog> failureLogs = log.getFailureLogs();
            List<FailureReason> failureReasons = failureLogs
                    .stream()
                    .map(failureLog ->
                            failureLog.getReason() == null
                                    ? FailureReason.builder()
                                        .id(null)
                                        .reason(failureLog.getCustomReason())
                                        .build()
                                    : failureLog.getReason())
                    .toList();

            HabitLogResponseDto habitLogResponseDto = HabitLogResponseDto.builder()
                    .id(log.getId())
                    .habitId(log.getHabit().getId())
                    .date(log.getDate())
                    .completed(log.isCompleted())
                    .failureReasons(failureReasons)
                    .build();

            habitLogResponseDtos.add(habitLogResponseDto);
        }
        return habitLogResponseDtos;
    }

    @Transactional
    public void deleteLog(Long logId) {
        habitLogRepository.deleteById(logId);
    }
}
