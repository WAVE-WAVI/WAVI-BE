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
public class ConsistencyIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitReportId")
    private HabitReport habitReport;

    @Column
    @JsonProperty("success_rate")
    private Double successRate;

    @Column
    @JsonProperty("display_message")
    private String displayMessage;

    public void setHabitReport(HabitReport habitReport) {
        this.habitReport = habitReport;
    }
}
