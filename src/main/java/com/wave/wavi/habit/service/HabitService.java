package com.wave.wavi.habit.service;

import com.wave.wavi.habit.dto.HabitRequestDto;
import com.wave.wavi.habit.dto.HabitResponseDto;
import com.wave.wavi.habit.model.Habit;
import com.wave.wavi.habit.model.HabitSchedule;
import com.wave.wavi.habit.model.StatusType;
import com.wave.wavi.habit.repository.HabitRepository;
import com.wave.wavi.habit.repository.HabitScheduleRepository;
import com.wave.wavi.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitScheduleRepository habitScheduleRepository;

    // 습관 등록 - 습관
    @Transactional
    public Long saveHabit(HabitRequestDto requestDto, User user) {
        Habit habit = Habit.builder()
                .name(requestDto.getName())
                .icon(requestDto.getIcon())
                .user(user)
                .status(StatusType.DEACTIVE)
                .build();
        return habitRepository.save(habit).getId();
    }

    // 습관 등록 - 계획
    @Transactional
    public void saveDaysOfWeek(HabitRequestDto requestDto, Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 습관을 찾지 못했습니다."));
        List<Integer> daysOfWeek = requestDto.getDayOfWeek();

        for (int dayOfWeek : daysOfWeek) {
            HabitSchedule habitSchedule = HabitSchedule.builder()
                    .habit(habit)
                    .dayOfWeek(dayOfWeek)
                    .build();
            habitScheduleRepository.save(habitSchedule);
        }

        updateHabitStatusToday(habit);
    }

    // 습관 수정
    @Transactional
    public void updateHabit(HabitRequestDto requestDto, Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 습관을 찾지 못했습니다."));
        if (requestDto.getName() != null) {
            habit.setName(requestDto.getName());
        }
        if (requestDto.getIcon() != null) {
            habit.setIcon(requestDto.getIcon());
        }
        if(requestDto.getDayOfWeek() != null) {
            List<Integer> daysOfWeek = requestDto.getDayOfWeek();
            List<HabitSchedule> oldSchedules = habitScheduleRepository.findByHabitId(habitId);

            for (HabitSchedule oldSchedule : oldSchedules) {
                if (!daysOfWeek.contains(oldSchedule.getDayOfWeek())) {
                    habitScheduleRepository.delete(oldSchedule);
                }
            }

            for (int dayOfWeek : daysOfWeek) {
                boolean exists = habitScheduleRepository.existsByHabitIdAndDayOfWeek(habitId, dayOfWeek);
                if (!exists) {
                    HabitSchedule habitSchedule = HabitSchedule.builder()
                            .habit(habit)
                            .dayOfWeek(dayOfWeek)
                            .build();
                    habitScheduleRepository.save(habitSchedule);
                }
            }

            updateHabitStatusToday(habit);
        }
    }

    // 습관 삭제
    @Transactional
    public void deleteHabit(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 습관을 찾지 못했습니다."));
        habit.setStatus(StatusType.DELETED);
    }

    // 단일 습관 조회
    @Transactional
    public HabitResponseDto getHabit(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 습관을 찾지 못했습니다."));
        return habitToDto(habit);
    }

    // 내 모든 습관 조회
    @Transactional
    public List<HabitResponseDto> getAllHabits(User user) {
        List<Habit> habits = habitRepository.findByUserId(user.getId());
        updateHabitStatusAll(habits);
        List<Habit> filteredHabits = habits
                .stream()
                .filter(habit -> habit.getStatus() != StatusType.DELETED)
                .toList();
        return habitsToDto(filteredHabits);
    }

    // 오늘의 습관 조회
    @Transactional
    public List<HabitResponseDto> getTodayHabits(User user) {
        List<Habit> habits = habitRepository.findByUserId(user.getId());
        updateHabitStatusAll(habits);
        List<Habit> filteredHabits = habits
                .stream()
                .filter(habit -> habit.getStatus() == StatusType.ACTIVE)
                .toList();
        return habitsToDto(filteredHabits);
    }

    // 습관 엔티티 -> DTO
    @Transactional
    public HabitResponseDto habitToDto (Habit habit) {
        List<Integer> daysOfWeek = new ArrayList<>();
        List<HabitSchedule> habitSchedules = habitScheduleRepository.findByHabitId(habit.getId());
        habitSchedules.forEach(habitSchedule -> daysOfWeek.add(habitSchedule.getDayOfWeek()));
        return HabitResponseDto.builder()
                .id(habit.getId())
                .name(habit.getName())
                .icon(habit.getIcon())
                .status(habit.getStatus())
                .dayOfWeek(daysOfWeek)
                .build();
    }

    // 습관 엔티티 리스트 -> DTO 리스트
    @Transactional
    public List<HabitResponseDto> habitsToDto(List<Habit> habits) {
        List<HabitResponseDto> habitResponseDtos = new ArrayList<>();
        habits.forEach(habit -> habitResponseDtos.add(habitToDto(habit)));
        return habitResponseDtos;
    }

    // 습관 계획에 따른 습관 활성화/비활성화
    @Transactional
    public void updateHabitStatusToday(Habit habit) {
        int today = LocalDate.now().getDayOfWeek().getValue();
        boolean exists = habitScheduleRepository.existsByHabitIdAndDayOfWeek(habit.getId(), today);
        habit.setStatus(exists ? StatusType.ACTIVE : StatusType.DEACTIVE);
    }

    @Transactional
    public void updateHabitStatusAll(List<Habit> habits){
        for (Habit habit : habits) {
            if (habit.getStatus() != StatusType.DELETED) {
                updateHabitStatusToday(habit);
            }
        }
    }
}
