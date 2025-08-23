package com.wave.wavi.user.dto;

import com.wave.wavi.user.model.GenderType;
import com.wave.wavi.user.model.JobType;
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
    private GenderType gender;
    private JobType job;
    private Long profileImage;
}
