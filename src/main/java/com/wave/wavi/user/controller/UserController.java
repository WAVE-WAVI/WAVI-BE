package com.wave.wavi.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.user.dto.*;
import com.wave.wavi.user.oauth.OAuthService;
import com.wave.wavi.user.security.UserDetailsImpl;
import com.wave.wavi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "회원가입, 로그인, 프로필 관리 등 사용자 관련 API")
public class UserController {
    private final UserService userService;
    private final OAuthService oAuthService;

    //회원 가입 요청
    @Operation(summary = "회원가입 시작(이메일 인증 요청)", description = "이메일 인증을 위해 인증 코드를 발송하는 API")
    @PostMapping("/signup/initiate")
    public ResponseDto<String> initiateSignup(@RequestBody SignupInitiateRequestDto requestDto) {
        userService.initiateSignup(requestDto);
        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("인증 이메일이 발송되었습니다. 10분 내에 인증을 완료해주세요.")
                .build();
    }

    //회원 가입
    @Operation(summary = "회원가입 완료", description = "이메일 인증 코드를 확인하고 회원 정보를 등록해 회원가입을 완료하는 API")
    @PostMapping("/signup/complete")
    public ResponseDto<String> completeSignup(@RequestBody UserSignupRequestDto requestDto) {
        userService.completeSignup(requestDto);
        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("회원 가입 완료")
                .build();
    }

    //로그인
    @Operation(summary = "일반 로그인", description = "이메일, 비밀번호로 로그인하고 jwt 토큰을 발급받는 API")
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
    @Operation(summary = "구글 로그인", description = "구글로부터 인증을 받아 로그인 후 jwt를 발급하는 API")
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
    @Operation(summary = "프로필 수정", description = "로그인 된 사용자의 프로필 정보를 수정하는 API")
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

    //비밀번호 수정 요청
    @Operation(summary = "비밀번호 변경 요청", description = "비밀번호 변경을 위해 인증 코드를 발송하는 API")
    @PostMapping("/password/request")
    public ResponseDto<String> requestUpdatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PasswordUpdateRequestDto requestDto) {

        String userEmail = userDetails.getUser().getEmail();
        userService.requestUpdatePassword(userEmail, requestDto);

        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("인증 이메일이 발송되었습니다. 10분 내에 인증을 완료해주세요.")
                .build();
    }

    //비밀번호 수정
    @Operation(summary = "비밀번호 변경", description = "이메일 인증 코드를 확인하고 비밀번호 변경을 완료하는 API")
    @PatchMapping("/password/confirm")
    public ResponseDto<String> confirmUpdatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PasswordVerificationRequestDto requestDto) {

        String userEmail = userDetails.getUser().getEmail();
        userService.confirmUpdatePassword(userEmail, requestDto);

        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("비밀번호가 성공적으로 변경되었습니다.")
                .build();
    }
}
