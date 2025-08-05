package com.wave.wavi.habit.repository;

import com.wave.wavi.habit.model.HabitSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface HabitScheduleRepository extends JpaRepository<HabitSchedule, Long> {

    boolean existsByHabitIdAndDayOfWeek(Long habitId, int dayOfWeek);
    List<HabitSchedule> findByHabitId(Long habitId);
}
