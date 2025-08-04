package com.wave.wavi.habit.repository;

import com.wave.wavi.habit.model.HabitSchedule;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HabitScheduleRepository extends JpaRepository<HabitSchedule, Long> {

    boolean existsByHabitIdAndDayOfWeek(Long habitId, int dayOfWeek);
}
