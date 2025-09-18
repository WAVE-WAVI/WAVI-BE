package com.wave.wavi.log.repository;

import com.wave.wavi.log.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    List<HabitLog> findByUserId(Long userId);

    List<HabitLog> findByUserIdAndDateBetween(Long userId, LocalDate dateAfter, LocalDate dateBefore);
}
