package com.wave.wavi.habit.service;

import com.wave.wavi.habit.dto.HabitSaveRequestDto;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitScheduleRepository habitScheduleRepository;

    @Transactional
    public Long saveHabit(HabitSaveRequestDto requestDto, User user) {
        Habit habit = Habit.builder()
                .name(requestDto.getName())
                .icon(requestDto.getIcon())
                .user(user)
                .status(StatusType.DEACTIVE)
                .build();
        return habitRepository.save(habit).getId();
    }

    @Transactional
    public void saveWeekOfDays(HabitSaveRequestDto requestDto, Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 습관을 찾지 못했습니다."));
        List<Integer> dayOfWeeks = requestDto.getDayOfWeek();

        for (int dayOfWeek : dayOfWeeks) {
            HabitSchedule habitSchedule = HabitSchedule.builder()
                    .habit(habit)
                    .dayOfWeek(dayOfWeek)
                    .build();
            habitScheduleRepository.save(habitSchedule);
        }

        LocalDate date = LocalDate.now();
        int today = date.getDayOfWeek().getValue();

        boolean exists = habitScheduleRepository.existsByHabitIdAndDayOfWeek(habit.getId(), today);
        habit.setStatus(exists ? StatusType.ACTIVE : StatusType.DEACTIVE);
    }
}
