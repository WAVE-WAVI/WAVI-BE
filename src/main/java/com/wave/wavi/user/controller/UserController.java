package com.wave.wavi.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.config.security.UserDetailsServiceImpl;
import com.wave.wavi.user.dto.*;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.oauth.OAuthService;
import com.wave.wavi.user.security.UserDetailsImpl;
import com.wave.wavi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OAuthService oAuthService;

    //회원 가입 요청
    @PostMapping("/signup-request")
    public ResponseDto<String> requestSignup(@RequestBody UserSignupRequestDto requestDto) {
        userService.requestSignup(requestDto);
        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("인증 이메일이 발송되었습니다. 10분 내에 인증을 완료해주세요.")
                .build();
    }

    //회원 가입
    @PostMapping("/signup-confirm")
    public ResponseDto<String> confirmSignup(@RequestBody EmailVerificationRequestDto requestDto) {
        userService.confirmSign(requestDto);
        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("회원 가입 완료")
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

    //개인 정보 수정
    @PatchMapping("/profile")
    public ResponseDto<String> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ProfileUpdateRequestDto requestDto) {
        String userEmail = userDetails.getUser().getEmail();
        userService.updateProfile(userEmail, requestDto);
        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("프로필 수정 성공")
                .build();
    }

    //비밀번호 수정
    @PostMapping("/password")
    public ResponseDto<String> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PasswordUpdateRequestDto requestDto) {

        String userEmail = userDetails.getUser().getEmail();
        userService.updatePassword(userEmail, requestDto);

        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("비밀번호 수정 성공")
                .build();
    }
}
