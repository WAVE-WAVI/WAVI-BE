package com.wave.wavi.main.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.config.jwt.JwtUtil;
import com.wave.wavi.main.dto.MainResponseDto;
import com.wave.wavi.main.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
@Tag(name = "메인화면 API", description = "메인화면에 필요한 정보를 불러오는 API")
public class MainController {

    private final MainService mainService;
    private final JwtUtil jwtUtil;

    @GetMapping("")
    @Operation(summary = "메인화면 정보 조회", description = "사용자 닉네임, 이미지, 오늘의 습관을 조회하는 API")
    public ResponseDto<MainResponseDto> main(HttpServletRequest request) {
        String email = jwtUtil.getUserInfoFromToken(jwtUtil.getTokenFromHeader(request)).getSubject();
        MainResponseDto mainResponseDto = mainService.main(email);
        return ResponseDto.<MainResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("메인화면 정보 불러오기 성공")
                .data(mainResponseDto)
                .build();
    }
}
