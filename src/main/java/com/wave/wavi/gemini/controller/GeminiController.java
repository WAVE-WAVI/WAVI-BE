package com.wave.wavi.gemini.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.gemini.dto.ChatAnalysisRequestDto;
import com.wave.wavi.gemini.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    @GetMapping("/habit")
    public ResponseDto<Object> analyzePrompt(@RequestBody ChatAnalysisRequestDto requestDto) {
        Object data = geminiService.analyzePrompt(requestDto);
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
