package com.pulseline.health.repository;

import com.pulseline.health.model.Vital;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VitalRepository extends JpaRepository<Vital, Long> {
    List<Vital> findAllByOrderByRecordedAtDesc();
}
