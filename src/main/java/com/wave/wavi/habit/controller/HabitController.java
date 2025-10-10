package com.wave.wavi.habit.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.config.jwt.JwtUtil;
import com.wave.wavi.habit.dto.ChatAnalysisRequestDto;
import com.wave.wavi.habit.dto.HabitRequestDto;
import com.wave.wavi.habit.dto.HabitResponseDto;
import com.wave.wavi.habit.service.ChatAnalysisService;
import com.wave.wavi.habit.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/habit")
@Tag(name = "습관 API", description = "습관 CRUD, 상태 갱신, 채팅 분석 API")
public class HabitController {

    private final HabitService habitService;
    private final JwtUtil jwtUtil;
    private final ChatAnalysisService chatAnalysisService;

    // 습관 등록
    @PostMapping("")
    @Operation(summary = "습관 등록", description = "새로운 습관을 등록하는 API")
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
    @Operation(summary = "습관 수정", description = "습관 내용을 수정하는 API")
    public ResponseDto<Object> updateHabit(@RequestBody HabitRequestDto requestDto, @PathVariable Long habitId) {
        habitService.updateHabit(requestDto, habitId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 수정 성공")
                .build();
    }

    // 습관 삭제
    @DeleteMapping("/{habitId}")
    @Operation(summary = "습관 삭제", description = "습관을 삭제하는 API")
    public ResponseDto<Object> deleteHabit(@PathVariable Long habitId) {
        habitService.deleteHabit(habitId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 삭제 성공")
                .build();
    }

    // 단일 습관 조회
    @GetMapping("/{habitId}")
    @Operation(summary = "단일 습관 조회", description = "한 개의 습관을 조회하는 API")
    public ResponseDto<HabitResponseDto> getHabit(@PathVariable Long habitId) {
        HabitResponseDto habit = habitService.getHabit(habitId);
        return ResponseDto.<HabitResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("습관 조회 성공")
                .data(habit)
                .build();
    }

    // 내 모든 습관 조회
    @GetMapping("")
    @Operation(summary = "모든 습관 조회", description = "사용자의 모든 습관을 조회하는 API")
    public ResponseDto<List<HabitResponseDto>> getAllHabits(HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        List<HabitResponseDto> habits = habitService.getAllHabits(email);
        return ResponseDto.<List<HabitResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .message("모든 습관 조회 성공")
                .data(habits)
                .build();
    }

    // 오늘의 습관 조회
    @GetMapping("/today")
    @Operation(summary = "오늘의 습관 조회", description = "사용자의 모든 습관을 조회하는 API")
    public ResponseDto<List<HabitResponseDto>> getTodayHabits(HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        List<HabitResponseDto> habits = habitService.getTodayHabits(email);
        return ResponseDto.<List<HabitResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .message("오늘의 습관 조회 성공")
                .data(habits)
                .build();
    }

    @PostMapping("/status")
    @Operation(summary = "습관 상태 갱신", description = "사용자의 습관 상태를 갱신하는 API")
    public ResponseDto<Object> updateHabitStatus(HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        habitService.updateHabitStatusDaily(email);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 상태 갱신 성공")
                .build();
    }

    @PostMapping("/chat")
    @Operation(summary = "AI 채팅 분석", description = "습관 등록 시 채팅 내용을 분석하는 API")
    public ResponseDto<Object> analyzePrompt(@RequestBody ChatAnalysisRequestDto requestDto) {
        Object data = chatAnalysisService.analyzePrompt(requestDto);
        if (data.getClass() == String.class) {
            return ResponseDto.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("메시지 분석 결과 정보 부족")
                    .data(data)
                    .build();
        }
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("메시지 분석 성공")
                .data(data)
                .build();
    }
}
