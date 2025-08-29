package com.wave.wavi.habit.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.config.jwt.JwtUtil;
import com.wave.wavi.habit.dto.HabitRequestDto;
import com.wave.wavi.habit.dto.HabitResponseDto;
import com.wave.wavi.habit.service.HabitService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/habit")
public class HabitController {

    private final HabitService habitService;
    private final JwtUtil jwtUtil;

    // 습관 등록
    @PostMapping("")
    public ResponseDto<Object> saveHabit(@RequestBody HabitRequestDto requestDto, HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        Long habitId = habitService.saveHabit(requestDto, email);
        habitService.saveDaysOfWeek(requestDto, habitId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 등록 성공")
                .build();
    }

    // 습관 수정
    @PatchMapping("/{habitId}")
    public ResponseDto<Object> updateHabit(@RequestBody HabitRequestDto requestDto, @PathVariable Long habitId) {
        habitService.updateHabit(requestDto, habitId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 수정 성공")
                .build();
    }

    // 습관 삭제
    @DeleteMapping("/{habitId}")
    public ResponseDto<Object> deleteHabit(@PathVariable Long habitId) {
        habitService.deleteHabit(habitId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 삭제 성공")
                .build();
    }

    // 단일 습관 조회
    @GetMapping("/{habitId}")
    public ResponseDto<Object> getHabit(@PathVariable Long habitId) {
        HabitResponseDto habit = habitService.getHabit(habitId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 조회 성공")
                .data(habit)
                .build();
    }

    // 내 모든 습관 조회
    @GetMapping("")
    public ResponseDto<Object> getAllHabits(HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        List<HabitResponseDto> habits = habitService.getAllHabits(email);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("모든 습관 조회 성공")
                .data(habits)
                .build();
    }

    // 오늘의 습관 조회
    @GetMapping("/today")
    public ResponseDto<Object> getTodayHabits(HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        List<HabitResponseDto> habits = habitService.getTodayHabits(email);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("오늘의 습관 조회 성공")
                .data(habits)
                .build();
    }
}
