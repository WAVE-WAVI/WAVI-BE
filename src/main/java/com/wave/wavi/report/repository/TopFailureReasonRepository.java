package com.wave.wavi.report.repository;

import com.wave.wavi.report.model.TopFailureReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopFailureReasonRepository extends JpaRepository<TopFailureReason, Long> {
}
