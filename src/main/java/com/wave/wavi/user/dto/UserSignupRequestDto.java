package com.wave.wavi.user.dto;

import com.wave.wavi.user.model.Gender;
import com.wave.wavi.user.model.Job;
import com.wave.wavi.user.model.LoginType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequestDto {
    private String email;
    private String password;
    private LoginType loginType;
    private String nickname;
    private Long birthYear;
    private Gender gender;
    private Job job;
    private Long profileImage;
}
