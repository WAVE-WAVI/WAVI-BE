package com.wave.wavi.log.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.log.dto.HabitLogRequestDto;
import com.wave.wavi.log.service.HabitLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/log")
public class HabitLogController {

    private final HabitLogService habitLogService;

    // 성공 기록
    @PostMapping("/success")
    public ResponseDto<Object> saveSuccess(@RequestBody HabitLogRequestDto requestDto) {
        habitLogService.saveSuccess(requestDto);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("성공 기록 저장 성공")
                .build();
    }
}
