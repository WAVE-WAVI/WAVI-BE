package com.wave.wavi.habit.repository;

import com.wave.wavi.habit.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserIdAndDeletedAtNull(Long userId);
}
