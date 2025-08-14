package com.wave.wavi.user.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.user.dto.UserSignupRequestDto;
import com.wave.wavi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseDto<Object> signup(@RequestBody UserSignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseDto.builder()
                .status(HttpStatus.CREATED.value())
                .message("회원 가입이 완료되었습니다.")
                .build();
    }
}
