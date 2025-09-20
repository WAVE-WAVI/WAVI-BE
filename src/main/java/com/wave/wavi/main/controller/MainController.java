package com.wave.wavi.main.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.config.jwt.JwtUtil;
import com.wave.wavi.main.dto.MainResponseDto;
import com.wave.wavi.main.service.MainService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainController {

    private final MainService mainService;
    private final JwtUtil jwtUtil;

    @GetMapping("")
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
