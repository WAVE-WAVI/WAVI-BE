package com.wave.wavi.habit.service;

import com.wave.wavi.habit.dto.HabitRequestDto;
import com.wave.wavi.habit.dto.HabitResponseDto;
import com.wave.wavi.habit.model.Habit;
import com.wave.wavi.habit.model.HabitSchedule;
import com.wave.wavi.habit.model.StatusType;
import com.wave.wavi.habit.repository.HabitRepository;
import com.wave.wavi.habit.repository.HabitScheduleRepository;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitScheduleRepository habitScheduleRepository;
    private final UserRepository userRepository;

    // 습관 등록 - 습관
    @Transactional
    public Long saveHabit(HabitRequestDto requestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email의 유저를 찾지 못했습니다."));
        Habit habit = Habit.builder()
                .name(requestDto.getName())
                .icon(requestDto.getIcon())
                .user(user)
                .status(StatusType.DEACTIVE)
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
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

        updateHabitStatus(habit);
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
        if (requestDto.getStartTime() != null) {
            habit.setStartTime(requestDto.getStartTime());
        }
        if (requestDto.getEndTime() != null) {
            habit.setEndTime(requestDto.getEndTime());
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

            updateHabitStatus(habit);
        }
    }

    // 습관 삭제
    @Transactional
    public void deleteHabit(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 습관을 찾지 못했습니다."));
        habit.setDeletedAt(LocalDateTime.now());
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
    public List<HabitResponseDto> getAllHabits(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email의 유저를 찾지 못했습니다."));
        List<Habit> habits = habitRepository.findByUserIdAndDeletedAtNull(user.getId());
        return habitsToDto(habits);
    }

    // 오늘의 습관 조회
    @Transactional
    public List<HabitResponseDto> getTodayHabits(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email의 유저를 찾지 못했습니다."));
        List<Habit> habits = habitRepository.findByUserIdAndDeletedAtNull(user.getId());
        List<Habit> filteredHabits = habits
                .stream()
                .filter(habit -> habit.getStatus() == StatusType.ACTIVE || habit.getStatus() == StatusType.COMPLETED)
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
                .startTime(habit.getStartTime())
                .endTime(habit.getEndTime())
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
    public void updateHabitStatus(Habit habit) {
        int today = LocalDate.now().getDayOfWeek().getValue();
        boolean exists = habitScheduleRepository.existsByHabitIdAndDayOfWeek(habit.getId(), today);
        habit.setStatus(exists ? StatusType.ACTIVE : StatusType.DEACTIVE);
    }

    @Transactional
    public void updateHabitStatusDaily(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email의 유저를 찾지 못했습니다."));
        if (user.getLastHabitUpdateDate() == null || !user.getLastHabitUpdateDate().equals(LocalDate.now())) {
            List<Habit> habits = habitRepository.findByUserIdAndDeletedAtNull(user.getId());
            for (Habit habit : habits) {
                updateHabitStatus(habit);
            }
            user.setLastHabitUpdateDate(LocalDate.now());
        } else {
            throw new IllegalArgumentException("오늘은 이미 상태를 갱신했습니다.");
        }
    }
}
