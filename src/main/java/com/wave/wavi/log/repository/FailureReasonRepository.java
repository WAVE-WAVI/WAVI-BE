package com.wave.wavi.log.repository;

import com.wave.wavi.log.model.FailureReason;
import com.wave.wavi.log.model.FailureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailureReasonRepository extends JpaRepository<FailureReason, Long> {
    List<FailureReason> findByType(FailureType type);
}
