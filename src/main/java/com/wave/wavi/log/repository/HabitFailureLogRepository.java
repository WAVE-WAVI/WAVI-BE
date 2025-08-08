package com.wave.wavi.log.repository;

import com.wave.wavi.log.model.HabitFailureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitFailureLogRepository extends JpaRepository<HabitFailureLog, Long> {
    List<HabitFailureLog> findByHabitLogId(Long habitLogId);
}
