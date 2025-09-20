package com.wave.wavi.report.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.config.jwt.JwtUtil;
import com.wave.wavi.report.dto.ReportRequestDto;
import com.wave.wavi.report.model.HabitReport;
import com.wave.wavi.report.model.ReportType;
import com.wave.wavi.report.service.HabitReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class HabitReportController {

    private final HabitReportService habitReportService;
    private final JwtUtil jwtUtil;

    @PostMapping("")
    public ResponseDto<Object> generateReport(@RequestBody ReportRequestDto requestDto, HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        habitReportService.generateReport(requestDto, email);
        return ResponseDto.builder()
                .status(HttpStatus.CREATED.value())
                .message("리포트 생성 성공")
                .build();
    }

    @GetMapping("")
    public ResponseDto<List<HabitReport>> getReports(
            @RequestParam (name = "type", required = false) ReportType type,
            @RequestParam (name = "startDate", required = false) LocalDate startDate,
            @RequestParam (name = "endDate", required = false) LocalDate endDate,
            HttpServletRequest request
    ) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        List<HabitReport> reports = habitReportService.getReport(type, startDate, endDate, email);
        return ResponseDto.<List<HabitReport>>builder()
                .status(HttpStatus.OK.value())
                .message("리포트 조회 성공")
                .data(reports)
                .build();
    }
}
