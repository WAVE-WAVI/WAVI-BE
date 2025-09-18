package com.wave.wavi.report.repository;

import com.wave.wavi.report.model.HabitReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitReportRepository extends JpaRepository<HabitReport, Long> {
}
