package com.wave.wavi.user.dto;

import com.wave.wavi.user.model.LoginType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequestDto {
    private String email;
    private String password;
    private String nickname;
    private Long profileImage;
    private LoginType loginType;
}
