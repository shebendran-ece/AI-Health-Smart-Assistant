package com.pulseline.health.service;

import com.pulseline.health.model.WaterLog;
import com.pulseline.health.repository.WaterLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WaterService {

    private final WaterLogRepository repository;
    private static final int MAX_CUPS = 8;

    public WaterService(WaterLogRepository repository) {
        this.repository = repository;
    }

    public WaterLog getToday() {
        return repository.findByDate(LocalDate.now())
                .orElseGet(() -> repository.save(new WaterLog(LocalDate.now(), 0)));
    }

    public WaterLog increment() {
        WaterLog log = getToday();
        int next = Math.min(MAX_CUPS, log.getCups() + 1);
        log.setCups(next);
        return repository.save(log);
    }

    public WaterLog reset() {
        WaterLog log = getToday();
        log.setCups(0);
        return repository.save(log);
    }
}
