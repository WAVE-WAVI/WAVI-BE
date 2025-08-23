package com.wave.wavi.habit.model;

import com.wave.wavi.common.BaseTimeEntity;
import com.wave.wavi.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Time;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Habit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private Long icon;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusType status;

    @Column
    private Long aim;

    @Column(nullable = false)
    private Time startTime;

    @Column(nullable = false)
    private Time endTime;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Habit(User user, Long icon, String name, StatusType status, Long aim, Time startTime, Time endTime) {
        this.user = user;
        this.icon = icon;
        this.name = name;
        this.status = status;
        this.aim = null;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deletedAt = null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setIcon(Long icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public void setAim(Long aim) {
        this.aim = aim;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
