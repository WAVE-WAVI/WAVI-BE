package com.wave.wavi.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.user.dto.UserLoginRequestDto;
import com.wave.wavi.user.dto.UserSignupRequestDto;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.oauth.OAuthService;
import com.wave.wavi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OAuthService oAuthService;

    //회원가입
    @PostMapping("/signup")
    public ResponseDto<Object> signup(@RequestBody UserSignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseDto.builder()
                .status(HttpStatus.CREATED.value())
                .message("회원 가입이 완료되었습니다.")
                .build();
    }

    //로그인
    @PostMapping("/login")
    public ResponseDto<Object> login(@RequestBody UserLoginRequestDto requestDto) {
        String token = userService.login(requestDto);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("로그인 성공")
                .data(token)
                .build();
    }

    //구글 로그인
    @GetMapping("/login/oauth2/code/google")
    public ResponseDto<String> googleLogin(@RequestParam(name = "code") String code) throws JsonProcessingException {
        String token = oAuthService.googleLogin(code);
        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("구글 로그인 성공")
                .data(token)
                .build();
    }
}
