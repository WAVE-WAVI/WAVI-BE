package com.wave.wavi.report.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wave.wavi.common.util.LongListConvert;
import com.wave.wavi.habit.model.Habit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"habitReport", "habit"})
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
    @Schema(type = "string")
    private LocalTime startTime;

    @Column
    @Schema(type = "string")
    private LocalTime endTime;

    @Column
    @Convert(converter = LongListConvert.class)
    private List<Long> dayOfWeek;

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

    public void setDayOfWeek(List<Long> dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
