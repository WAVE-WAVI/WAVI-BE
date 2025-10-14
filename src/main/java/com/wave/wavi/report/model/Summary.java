package com.wave.wavi.report.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"habitReport"})
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitReportId")
    private HabitReport habitReport;

    @Column(columnDefinition = "TEXT")
    private String consistency;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("failure_reasons")
    private String failureReasons;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("daily_pattern")
    private String dailyPattern;

    @Column(columnDefinition = "TEXT")
    private String courage;

    public void setHabitReport(HabitReport habitReport) {
        this.habitReport = habitReport;
    }
}
