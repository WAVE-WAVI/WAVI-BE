package com.wave.wavi.report.model;

import com.wave.wavi.habit.model.Habit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitReportId")
    private HabitReport habitReport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitId", nullable = false)
    private Habit habit;

    @Column
    private String name;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Column
    private String dayOfWeek;

    public void setId(Long id) {
        this.id = id;
    }

    public void setHabitReport(HabitReport habitReport) {
        this.habitReport = habitReport;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
