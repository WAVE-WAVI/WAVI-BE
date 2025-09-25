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
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;

    //회원 가입 요청
    public void requestSignup(UserSignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        String hashedPassword = passwordEncoder.encode(requestDto.getPassword());

        Map<String, Object> signupData = new HashMap<>();
        signupData.put("password", hashedPassword);
        signupData.put("nickname", requestDto.getNickname());
        signupData.put("birthYear", requestDto.getBirthYear());
        signupData.put("gender", requestDto.getGender());
        signupData.put("job", requestDto.getJob());
        signupData.put("profileImage", requestDto.getProfileImage());
        signupData.put("loginType", LoginType.NORMAL);
        signupData.put("verificationCode", verificationCode);

        String redisKey = "signup" + requestDto.getEmail();
        redisTemplate.opsForValue().set(redisKey, signupData, 10, TimeUnit.MINUTES);
        emailService.sendEmail(requestDto.getEmail(),
                "[WAVI] 회원가입을 위한 이메일 인증 코드입니다.",
                "WAVI 서비스에 가입해 주셔서 감사합니다.\n인증 코드: [" + verificationCode + "]");
    }

    //회원 가입
    @Transactional
    public void confirmSign(EmailVerificationRequestDto requestDto) {
        String redisKey = "signup" + requestDto.getEmail();
        Map<String, Object> signupData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
        if (signupData == null) {
            throw new IllegalArgumentException("인증 시간이 만료되었거나 잘못된 요청입니다.");
        }
        if (!signupData.get("verificationCode").equals(requestDto.getCode())) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        User user = User.builder()
                .email(requestDto.getEmail())
                .password((String) signupData.get("password"))
                .nickname((String) signupData.get("nickname"))
                .birthYear((Long) signupData.get("birthYear"))
                .gender((GenderType) signupData.get("gender"))
                .job((JobType) signupData.get("job"))
                .profileImage((Long) signupData.get("profileImage"))
                .loginType((LoginType) signupData.get("loginType"))
                .build();

        userRepository.save(user);
        redisTemplate.delete(redisKey);
    }

    //로그인
    @Transactional(readOnly = true)
    public String login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
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

    public void requestUpdatePassword(String email, PasswordUpdateRequestDto requestDto) {
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(),  user.getPassword())) {
            throw new IllegalArgumentException("현재 비밃번호가 일치하지 않습니다.");
        }
        if (passwordEncoder.matches(requestDto.getNewPassword(),  user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 같을 수 없습니다.");
        }

        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        String newHashedPassword = passwordEncoder.encode(requestDto.getNewPassword());

        Map<String, String> passwordChangeData = new HashMap<>();
        passwordChangeData.put("newPassword", newHashedPassword);
        passwordChangeData.put("verificationCode", verificationCode);

        String redisKey = "pwchange" + email;
        redisTemplate.opsForValue().set(redisKey, passwordChangeData, 10, TimeUnit.MINUTES);
        emailService.sendEmail(email,
                "[WAVI] 비밀번호 변경을 위한 이메일 인증 코드입니다.",
                "WAVI 서비스를 이용해 주셔서 감사합니다.\n인증 코드: [" + verificationCode + "]");
    }

    @Transactional
    public void confirmUpdatePassword(String email, PasswordVerificationRequestDto requestDto) {
        String redisKey = "pwchange" + email;
        Map<String, String> passwordChangeData = (Map<String, String>) redisTemplate.opsForValue().get(redisKey);

        if (passwordChangeData == null) {
            throw new IllegalArgumentException("인증 시간이 만료되었거나 잘못된 요청입니다.");
        }
        if (!passwordChangeData.get("verificationCode").equals(requestDto.getCode())) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        user.setPassword(passwordChangeData.get("newPassword"));
        redisTemplate.delete(redisKey);
    }
}

