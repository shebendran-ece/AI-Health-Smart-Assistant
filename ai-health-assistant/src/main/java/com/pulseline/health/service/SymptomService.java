package com.pulseline.health.service;

import com.pulseline.health.dto.SymptomResponse;
import com.pulseline.health.model.SymptomLog;
import com.pulseline.health.repository.SymptomLogRepository;
import org.springframework.stereotype.Service;

@Service
public class SymptomService {

    private final GeminiService geminiService;
    private final SymptomLogRepository repository;

    public SymptomService(GeminiService geminiService, SymptomLogRepository repository) {
        this.geminiService = geminiService;
        this.repository = repository;
    }

    public SymptomResponse handle(String message) {
        SymptomResponse response = geminiService.analyze(message);
        repository.save(new SymptomLog(message, response.getReply(), response.isUrgent(), response.getSource()));
        return response;
    }
}
