package com.wave.wavi.main.service;

import com.wave.wavi.habit.dto.HabitResponseDto;
import com.wave.wavi.habit.model.Habit;
import com.wave.wavi.habit.model.StatusType;
import com.wave.wavi.habit.repository.HabitRepository;
import com.wave.wavi.habit.service.HabitService;
import com.wave.wavi.main.dto.MainResponseDto;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitService habitService;

    @Transactional
    public MainResponseDto main(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email의 유저를 찾지 못했습니다."));
        List<Habit> habits = habitRepository.findByUserIdAndDeletedAtNull(user.getId());
        List<Habit> filteredHabits = habits
                .stream()
                .filter(habit -> habit.getStatus() == StatusType.ACTIVE || habit.getStatus() == StatusType.COMPLETED)
                .toList();
        List<HabitResponseDto> todayHabits = habitService.habitsToDto(filteredHabits);
        return MainResponseDto.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .todayHabits(todayHabits)
                .build();
    }

}
