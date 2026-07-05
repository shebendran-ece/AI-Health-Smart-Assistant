package com.pulseline.health.service;

import com.pulseline.health.model.Vital;
import com.pulseline.health.repository.VitalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VitalService {

    private final VitalRepository repository;

    // Typical adult reference ranges (rough screening bands only - not clinical thresholds).
    private static final Map<String, double[]> RANGES = Map.of(
            "bp", new double[]{90, 130},
            "hr", new double[]{50, 100},
            "temp", new double[]{36.1, 37.5},
            "spo2", new double[]{95, 100}
    );

    private static final Map<String, String> UNITS = Map.of(
            "bp", "mmHg",
            "hr", "bpm",
            "temp", "°C",
            "spo2", "%"
    );

    public VitalService(VitalRepository repository) {
        this.repository = repository;
    }

    public List<Vital> getAll() {
        return repository.findAllByOrderByRecordedAtDesc();
    }

    public Vital add(String type, Double value) {
        double[] range = RANGES.getOrDefault(type, new double[]{Double.MIN_VALUE, Double.MAX_VALUE});
        String unit = UNITS.getOrDefault(type, "");
        boolean flagged = value < range[0] || value > range[1];
        Vital vital = new Vital(type, value, unit, flagged);
        return repository.save(vital);
    }
}
