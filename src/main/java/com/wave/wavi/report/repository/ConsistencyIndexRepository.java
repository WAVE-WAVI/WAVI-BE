package com.wave.wavi.report.repository;

import com.wave.wavi.report.model.ConsistencyIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsistencyIndexRepository extends JpaRepository<ConsistencyIndex, Long> {
}
