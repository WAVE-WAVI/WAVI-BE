package com.wave.wavi.log.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.log.dto.HabitFailureLogRequestDto;
import com.wave.wavi.log.dto.HabitLogResponseDto;
import com.wave.wavi.log.model.FailureReason;
import com.wave.wavi.log.service.HabitLogService;
import com.wave.wavi.user.model.LoginType;
import com.wave.wavi.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/log")
public class HabitLogController {

    private final HabitLogService habitLogService;

    // 성공 기록
    @PostMapping("/success/{habitId}")
    public ResponseDto<Object> saveSuccess(@PathVariable Long habitId) {
        habitLogService.saveSuccess(habitId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("성공 기록 저장 성공")
                .build();
    }

    // 실패 기록
    @PostMapping("/failure/{habitId}")
    public ResponseDto<Object> saveFailure(@PathVariable Long habitId, @RequestBody HabitFailureLogRequestDto requestDto) {
        habitLogService.saveFailure(habitId, requestDto);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("실패 기록 저장 성공")
                .build();
    }

    // 실패 이유 조회(목록용)
    @GetMapping("/failure")
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
    public ResponseDto<Object> getLogs(
            @RequestParam (name = "habitId", required = false) Long habitId,
            @RequestParam (name = "startDate", required = false) LocalDate startDate,
            @RequestParam (name = "endDate", required = false) LocalDate endDate,
            @RequestParam (name = "completed", required = false) Boolean completed/*,
            @AuthenticationPrincipal PrincipalDetail principal*/
    ) {
        // 더미 유저 정보
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .loginType(LoginType.NORMAL)
                .nickname("test")
                .password("1234")
                .profileImage(Long.valueOf(2))
                .build();

        List<HabitLogResponseDto> logs = habitLogService.getLogs(habitId, startDate, endDate, completed, user.getId());
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("기록 조회 성공")
                .data(logs)
                .build();
    }

    // 기록 삭제
    @DeleteMapping("/{logId}")
    public ResponseDto<Object> deleteLog(@PathVariable Long logId) {
        habitLogService.deleteLog(logId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("기록 삭제 성공")
                .build();
    }
}
