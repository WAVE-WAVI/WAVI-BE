package com.wave.wavi.user.service;

import com.wave.wavi.user.dto.UserLoginRequestDto;
import com.wave.wavi.user.dto.UserSignupRequestDto;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //회원가입
    @Transactional
    public User signup(UserSignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .loginType(requestDto.getLoginType())
                .nickname(requestDto.getNickname())
                .birthYear(requestDto.getBirthYear())
                .gender(requestDto.getGender())
                .job(requestDto.getJob())
                .profileImage(requestDto.getProfileImage())
                .build();
        return userRepository.save(user);
    }

    //로그인
    @Transactional(readOnly = true)
    public User login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
