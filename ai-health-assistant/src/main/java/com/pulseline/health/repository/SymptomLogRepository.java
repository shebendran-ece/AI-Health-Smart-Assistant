package com.pulseline.health.repository;

import com.pulseline.health.model.SymptomLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SymptomLogRepository extends JpaRepository<SymptomLog, Long> {
    List<SymptomLog> findAllByOrderByCreatedAtDesc();
}
