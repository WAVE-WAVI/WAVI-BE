package com.wave.wavi.report.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.config.jwt.JwtUtil;
import com.wave.wavi.report.dto.ReportRequestDto;
import com.wave.wavi.report.service.HabitReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class HabitReportController {

    private final HabitReportService habitReportService;
    private final JwtUtil jwtUtil;
    private Logger log;

    @PostMapping("")
    public ResponseDto<Object> generateReport(@RequestBody ReportRequestDto requestDto, HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        habitReportService.generateReport(requestDto, email);
        return ResponseDto.builder()
                .status(HttpStatus.CREATED.value())
                .message("리포트 생성 성공")
                .build();
    }
}
