package com.pulseline.health.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseline.health.dto.SymptomResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Keywords that should never wait on a model call - these get an immediate,
     * hard-coded urgent response instead of going to Gemini.
     */
    private static final List<String> URGENT_KEYWORDS = Arrays.asList(
            "chest pain", "can't breathe", "cannot breathe", "difficulty breathing",
            "severe bleeding", "suicidal", "can't stay awake", "unresponsive",
            "stroke", "face drooping", "slurred speech", "severe allergic",
            "anaphylaxis", "crushing pain", "blue lips"
    );

    private static final String SYSTEM_PROMPT = """
            You are a general wellness assistant inside a health tracking app called Pulseline.
            Rules you must always follow:
            1. You never provide a diagnosis or claim to know what condition someone has.
            2. You give general, cautious, non-prescriptive self-care information (rest, hydration,
               when to monitor, general over-the-counter category names only - no specific dosing).
            3. You always close by encouraging the person to see a licensed clinician for anything
               persistent, severe, or worsening.
            4. If the message describes anything that sounds like a medical emergency, tell the
               person clearly to contact emergency services immediately instead of continuing the chat.
            5. Keep the reply under 120 words, plain language, warm but not saccharine.
            """;

    public SymptomResponse analyze(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        boolean urgent = URGENT_KEYWORDS.stream().anyMatch(lower::contains);

        if (urgent) {
            return new SymptomResponse(
                    "This sounds like it could be a medical emergency. Please contact your local " +
                    "emergency number or get to the nearest emergency department right away. Don't wait " +
                    "on a chat reply for this.",
                    true,
                    "urgent-filter"
            );
        }

        try {
            String replyText = callGemini(message);
            return new SymptomResponse(replyText, false, "gemini");
        } catch (Exception e) {
            // Network issues, missing/invalid API key, quota limits, etc. Fail safe with a
            // generic, still-useful response rather than surfacing an error to the user.
            return new SymptomResponse(
                    "I couldn't reach the AI service just now, so here's general guidance: track when " +
                    "this started, how severe it feels, and anything that makes it better or worse. " +
                    "If it's new, severe, or getting worse, please check in with a clinician.",
                    false,
                    "fallback"
            );
        }
    }

    private String callGemini(String message) throws Exception {
        String requestBody = """
                {
                  "system_instruction": {
                    "parts": [{ "text": %s }]
                  },
                  "contents": [
                    { "role": "user", "parts": [{ "text": %s }] }
                  ]
                }
                """.formatted(
                        mapper.writeValueAsString(SYSTEM_PROMPT),
                        mapper.writeValueAsString(message)
                );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?key=" + apiKey))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            throw new RuntimeException("Gemini API returned status " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = mapper.readTree(response.body());
        JsonNode textNode = root.path("candidates").path(0)
                .path("content").path("parts").path(0).path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            throw new RuntimeException("Gemini API returned an empty response");
        }
        return textNode.asText().trim();
    }
}
