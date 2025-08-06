package com.wave.wavi.habit.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "habit_schedule", uniqueConstraints = {
        @UniqueConstraint(
                name = "HABITSCHEDULE_UNIQUE",
                columnNames = {"habit_id", "day_of_week"}
        )
})
public class HabitSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitId")
    private Habit habit;

    @Column(nullable = false)
    private int dayOfWeek;

    @Builder
    public HabitSchedule(Habit habit, int dayOfWeek) {
        this.habit = habit;
        this.dayOfWeek = dayOfWeek;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
