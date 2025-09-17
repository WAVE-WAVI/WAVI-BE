package com.wave.wavi.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wave.wavi.common.email.EmailService;
import com.wave.wavi.config.jwt.JwtUtil;
import com.wave.wavi.user.dto.*;
import com.wave.wavi.user.model.GenderType;
import com.wave.wavi.user.model.JobType;
import com.wave.wavi.user.model.LoginType;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    //회원가입
    @Transactional
    public void signup(UserSignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String vertificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .loginType(LoginType.NORMAL)
                .nickname(requestDto.getNickname())
                .birthYear(requestDto.getBirthYear())
                .gender(requestDto.getGender())
                .job(requestDto.getJob())
                .profileImage(requestDto.getProfileImage())
                .emailVerified(false)
                .verificationCode(vertificationCode)
                .build();

        userRepository.save(user);

        emailService.sendVertificationEmail(user.getEmail(),  vertificationCode);
    }

    //로그인
    @Transactional(readOnly = true)
    public String login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        return jwtUtil.createToken(user.getEmail());
    }

    //개인 정보 수정
    @Transactional
    public void updateProfile(String email, ProfileUpdateRequestDto requestDto) {
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (requestDto.getNickname() != null) {
            user.setNickname(requestDto.getNickname());
        }
        if (requestDto.getProfileImage() != null) {
            user.setProfileImage(requestDto.getProfileImage());
        }
        if (requestDto.getBirthYear() != null) {
            user.setBirthYear(requestDto.getBirthYear());
        }
        if (requestDto.getGender() != null) {
            user.setGender(requestDto.getGender());
        }
        if (requestDto.getJob() != null) {
            user.setJob(requestDto.getJob());
        }
    }

    //비밀번호 수정
    @Transactional
    public void updatePassword(String email, PasswordUpdateRequestDto requestDto) {
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 같을 수 없습니다.");
        }

        String newHashedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        user.setPassword(newHashedPassword);
    }

    //이메일 인증
    @Transactional
    public void verifyEmail(EmailVerificationRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("이미 인증이 완료된 사용자입니다.");
        }

        if (!user.getVerificationCode().equals(requestDto.getCode())) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
    }
}
