package com.pulseline.health.controller;

import com.pulseline.health.dto.MedicationRequest;
import com.pulseline.health.model.Medication;
import com.pulseline.health.service.MedicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@CrossOrigin(origins = "*")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    public List<Medication> getAll() {
        return medicationService.getAll();
    }

    @PostMapping
    public Medication add(@Valid @RequestBody MedicationRequest request) {
        return medicationService.add(request.getName(), request.getTime());
    }

    @PutMapping("/{id}/toggle")
    public Medication toggle(@PathVariable Long id) {
        return medicationService.toggle(id);
    }
}
