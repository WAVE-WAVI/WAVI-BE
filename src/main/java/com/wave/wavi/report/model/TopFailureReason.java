package com.wave.wavi.report.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wave.wavi.habit.model.Habit;
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
@JsonIgnoreProperties({"habitReport", "habit"})
public class TopFailureReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitReportId", nullable = false)
    private HabitReport habitReport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitId", nullable = false)
    private Habit habit;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private Long priority;

    public void setId(Long id) {
        this.id = id;
    }

    public void setHabitReport(HabitReport habitReport) {
        this.habitReport = habitReport;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }
}
