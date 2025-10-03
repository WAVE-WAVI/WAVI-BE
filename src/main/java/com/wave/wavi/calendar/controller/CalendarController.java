package com.wave.wavi.calendar.controller;

import com.wave.wavi.calendar.dto.SuccessRateResponseDto;
import com.wave.wavi.calendar.service.CalendarService;
import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
@Tag(name = "캘린더 API", description = "기간 별 습관 달성률 확인 등 캘린더 관련 API")
public class CalendarController {
    private final CalendarService calendarService;

    @Operation(summary = "습관 성공률 조회", description = "특정 기간 동안의 습관 성공률을 백분율로 조회하는 API")
    @GetMapping("/success-rate")
    public ResponseDto<SuccessRateResponseDto> getSuccessRate(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam (name = "startDate", required = false) LocalDate startDate,
            @RequestParam (name = "endDate", required = false) LocalDate endDate
            ) {
        String email = userDetails.getUser().getEmail();
        SuccessRateResponseDto successRateDto = calendarService.getSuccessRate(startDate, endDate, email);
        return ResponseDto.<SuccessRateResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("습관 성공률 조회 성공")
                .data(successRateDto)
                .build();
    }
}
