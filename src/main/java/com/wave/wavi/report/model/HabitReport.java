package com.wave.wavi.report.model;

import com.wave.wavi.common.BaseTimeEntity;
import com.wave.wavi.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private String summary;

    @Column
    private Time totalTime;

    @Column
    private String topFailureReasons;

    @Column
    private String recommendations;

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTotalTime(Time totalTime) {
        this.totalTime = totalTime;
    }

    public void setTopFailureReasons(String topFailureReasons) {
        this.topFailureReasons = topFailureReasons;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
}
