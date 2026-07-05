package com.pulseline.health.controller;

import com.pulseline.health.model.WaterLog;
import com.pulseline.health.service.WaterService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/water")
@CrossOrigin(origins = "*")
public class WaterController {

    private final WaterService waterService;

    public WaterController(WaterService waterService) {
        this.waterService = waterService;
    }

    @GetMapping("/today")
    public WaterLog today() {
        return waterService.getToday();
    }

    @PostMapping("/increment")
    public WaterLog increment() {
        return waterService.increment();
    }

    @PostMapping("/reset")
    public WaterLog reset() {
        return waterService.reset();
    }
}
