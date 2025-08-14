package com.wave.wavi.user.service;

import com.wave.wavi.user.dto.UserSignupRequestDto;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                .nickname(requestDto.getNickname())
                .loginType(requestDto.getLoginType())
                .profileImage(requestDto.getProfileImage())
                .build();
        return userRepository.save(user);
    }
}
