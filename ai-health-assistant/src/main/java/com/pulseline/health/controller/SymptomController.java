package com.pulseline.health.controller;

import com.pulseline.health.dto.SymptomRequest;
import com.pulseline.health.dto.SymptomResponse;
import com.pulseline.health.service.SymptomService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/symptom-check")
@CrossOrigin(origins = "*")
public class SymptomController {

    private final SymptomService symptomService;

    public SymptomController(SymptomService symptomService) {
        this.symptomService = symptomService;
    }

    @PostMapping
    public SymptomResponse check(@Valid @RequestBody SymptomRequest request) {
        return symptomService.handle(request.getMessage());
    }
}
