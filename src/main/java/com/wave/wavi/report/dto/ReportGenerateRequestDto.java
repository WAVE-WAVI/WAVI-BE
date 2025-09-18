package com.wave.wavi.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wave.wavi.report.model.ReportType;
import com.wave.wavi.user.model.GenderType;
import com.wave.wavi.user.model.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportGenerateRequestDto {
    @JsonProperty("user_id")
    private Long userId;

    private String nickname;

    @JsonProperty("birth_year")
    private Long birthYear;

    private GenderType gender;

    private JobType job;

    private ReportType type;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    private List<Habit> habits;

    @Builder
    @Getter
    public static class Habit {
        @JsonProperty("habit_id")
        private Long habitId;

        private String name;

        @JsonProperty("day_of_week")
        private List<Integer> dayOfWeek;

        @JsonProperty("start_time")
        private LocalTime startTime;

        @JsonProperty("end_time")
        private LocalTime endTime;

        @JsonProperty("habit_log")
        private List<HabitLog> habitLog;
    }

    @Builder
    @Getter
    public static class HabitLog {
        private LocalDate date;

        private boolean completed;

        @JsonProperty("failure_reason")
        private List<String> failureReason;
    }
}
