package com.wave.wavi.user.model;

import com.wave.wavi.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    @Column(nullable = false, length =  50)
    private String nickname;

    @Column(nullable = false)
    private Long birthYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderType gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType job;

    @Column(nullable = false)
    private Long profileImage;

    @Column
    private LocalDate lastHabitUpdateDate;

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfileImage(Long profileImage) {
        this.profileImage = profileImage;
    }

    public void setBirthYear(Long birthYear) {
        this.birthYear = birthYear;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public void setJob(JobType job) {
        this.job = job;
    }

    public void setLastHabitUpdateDate(LocalDate lastHabitUpdateDate) {
        this.lastHabitUpdateDate = lastHabitUpdateDate;
    }
}
