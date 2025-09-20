package com.wave.wavi.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.config.security.UserDetailsServiceImpl;
import com.wave.wavi.user.dto.PasswordUpdateRequestDto;
import com.wave.wavi.user.dto.ProfileUpdateRequestDto;
import com.wave.wavi.user.dto.UserLoginRequestDto;
import com.wave.wavi.user.dto.UserSignupRequestDto;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.oauth.OAuthService;
import com.wave.wavi.user.security.UserDetailsImpl;
import com.wave.wavi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseDto<String> login(@RequestBody UserLoginRequestDto requestDto) {
        String token = userService.login(requestDto);
        return ResponseDto.<String>builder()
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
