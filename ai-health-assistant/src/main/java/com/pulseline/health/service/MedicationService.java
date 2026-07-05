package com.pulseline.health.service;

import com.pulseline.health.model.Medication;
import com.pulseline.health.repository.MedicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicationService {

    private final MedicationRepository repository;

    public MedicationService(MedicationRepository repository) {
        this.repository = repository;
    }

    public List<Medication> getAll() {
        return repository.findAllByOrderByCreatedAtAsc();
    }

    public Medication add(String name, String time) {
        Medication med = new Medication(name, (time == null || time.isBlank()) ? "—" : time);
        return repository.save(med);
    }

    public Medication toggle(Long id) {
        Medication med = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medication not found: " + id));
        med.setTaken(!med.isTaken());
        return repository.save(med);
    }
}
