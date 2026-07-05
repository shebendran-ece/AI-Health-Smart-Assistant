package com.pulseline.health.controller;

import com.pulseline.health.dto.VitalRequest;
import com.pulseline.health.model.Vital;
import com.pulseline.health.service.VitalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vitals")
@CrossOrigin(origins = "*")
public class VitalController {

    private final VitalService vitalService;

    public VitalController(VitalService vitalService) {
        this.vitalService = vitalService;
    }

    @GetMapping
    public List<Vital> getAll() {
        return vitalService.getAll();
    }

    @PostMapping
    public Vital add(@Valid @RequestBody VitalRequest request) {
        return vitalService.add(request.getType(), request.getValue());
    }
}
