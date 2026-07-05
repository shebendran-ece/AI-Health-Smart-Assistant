package com.pulseline.health.repository;

import com.pulseline.health.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findAllByOrderByCreatedAtAsc();
}
