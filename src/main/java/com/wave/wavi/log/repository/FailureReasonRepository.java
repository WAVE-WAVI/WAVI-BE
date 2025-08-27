package com.wave.wavi.log.repository;

import com.wave.wavi.log.model.FailureReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailureReasonRepository extends JpaRepository<FailureReason, Long> {
}
