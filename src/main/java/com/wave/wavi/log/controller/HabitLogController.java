package com.wave.wavi.log.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.config.jwt.JwtUtil;
import com.wave.wavi.log.dto.HabitFailureLogRequestDto;
import com.wave.wavi.log.dto.HabitLogResponseDto;
import com.wave.wavi.log.model.FailureReason;
import com.wave.wavi.log.service.HabitLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/log")
@Tag(name = "습관 기록 API", description = "습관 기록 CRUD, 실패 이유 조회 API")
public class HabitLogController {

    private final HabitLogService habitLogService;
    private final JwtUtil jwtUtil;

    // 성공 기록
    @PostMapping("/success/{habitId}")
    @Operation(summary = "성공 기록 등록", description = "습관의 성공을 기록하는 API")
    public ResponseDto<Object> saveSuccess(@PathVariable Long habitId, HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        habitLogService.saveSuccess(habitId, email);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("성공 기록 저장 성공")
                .build();
    }

    // 실패 기록
    @PostMapping("/failure/{habitId}")
    @Operation(summary = "실패 기록 등록", description = "습관의 실패를 기록하는 API")
    public ResponseDto<Object> saveFailure(@PathVariable Long habitId, @RequestBody HabitFailureLogRequestDto requestDto, HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        habitLogService.saveFailure(habitId, email, requestDto);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("실패 기록 저장 성공")
                .build();
    }

    // 실패 이유 조회(목록용)
    @GetMapping("/failure")
    @Operation(summary = "실패 이유 목록 조회", description = "미리 정의된 실패 이유 목록을 조회하는 API")
    public ResponseDto<Object> getFailureReasons() {
        List<FailureReason> failureReasons = habitLogService.getFailureReasons();
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("실패 목록 조회 성공")
                .data(failureReasons)
                .build();
    }

    // 조건별 모든 기록 조회
    @GetMapping("")
    @Operation(summary = "조건별 모든 기록 조회", description = "특정 습관, 날짜 범위, 성공 여부로 기록을 조회하는 API")
    public ResponseDto<Object> getLogs(
            @RequestParam (name = "habitId", required = false) Long habitId,
            @RequestParam (name = "startDate", required = false) LocalDate startDate,
            @RequestParam (name = "endDate", required = false) LocalDate endDate,
            @RequestParam (name = "completed", required = false) Boolean completed,
            HttpServletRequest request
    ) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        List<HabitLogResponseDto> logs = habitLogService.getLogs(habitId, startDate, endDate, completed, email);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("기록 조회 성공")
                .data(logs)
                .build();
    }

    // 기록 삭제
    @DeleteMapping("/{logId}")
    @Operation(summary = "기록 삭제", description = "기록을 삭제하는 API")
    public ResponseDto<Object> deleteLog(@PathVariable Long logId) {
        habitLogService.deleteLog(logId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("기록 삭제 성공")
                .build();
    }
}
