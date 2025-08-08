package com.wave.wavi.log.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.log.dto.HabitFailureLogRequestDto;
import com.wave.wavi.log.dto.HabitSuccessLogRequestDto;
import com.wave.wavi.log.model.FailureReason;
import com.wave.wavi.log.model.FailureType;
import com.wave.wavi.log.service.HabitLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/log")
public class HabitLogController {

    private final HabitLogService habitLogService;

    // 성공 기록
    @PostMapping("/success")
    public ResponseDto<Object> saveSuccess(@RequestBody HabitSuccessLogRequestDto requestDto) {
        habitLogService.saveSuccess(requestDto);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("성공 기록 저장 성공")
                .build();
    }

    // 실패 기록
    @PostMapping("/failure")
    public ResponseDto<Object> saveFailure(@RequestBody HabitFailureLogRequestDto requestDto) {
        habitLogService.saveFailure(requestDto);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("실패 기록 저장 성공")
                .build();
    }

    // 실패 이유 조회(목록용)
    @GetMapping("/failure")
    public ResponseDto<Object> getFailureReasons(@RequestParam (name = "type", required = true) FailureType type) {
        List<FailureReason> failureReasons = habitLogService.getFailureReasons(type);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("실패 목록 조회 성공")
                .data(failureReasons)
                .build();
    }
}
