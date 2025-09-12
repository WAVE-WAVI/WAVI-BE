package com.wave.wavi.user.dto;

import com.wave.wavi.user.model.GenderType;
import com.wave.wavi.user.model.JobType;
import lombok.Getter;

@Getter
public class ProfileUpdateRequestDto {
    private String nickname;
    private Long profileImage;
    private Long birthYear;
    private GenderType gender;
    private JobType job;
}
