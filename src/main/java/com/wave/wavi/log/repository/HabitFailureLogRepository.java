package com.wave.wavi.log.repository;

import com.wave.wavi.log.model.HabitFailureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitFailureLogRepository extends JpaRepository<HabitFailureLog, Long> {
}
