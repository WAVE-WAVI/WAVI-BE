package com.wave.wavi.report.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wave.wavi.common.BaseTimeEntity;
import com.wave.wavi.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"user"})
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
    @Schema(type = "string")
    private LocalDate startDate;

    @Column(nullable = false)
    @Schema(type = "string")
    private LocalDate endDate;

    @OneToOne(mappedBy = "habitReport", fetch = FetchType.EAGER)
    private Summary summary;

    @OneToMany(mappedBy = "habitReport", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<TopFailureReason> topFailureReasons = new ArrayList<>();

    @OneToMany(mappedBy = "habitReport", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Recommendation> recommendation = new ArrayList<>();

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

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public void setTopFailureReasons(List<TopFailureReason> topFailureReasons) {
        this.topFailureReasons = topFailureReasons;
    }

    public void setRecommendation(List<Recommendation> recommendation) {
        this.recommendation = recommendation;
    }
}
