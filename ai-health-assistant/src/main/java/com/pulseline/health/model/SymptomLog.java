package com.pulseline.health.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "symptom_logs")
public class SymptomLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reply;

    @Column(nullable = false)
    private boolean urgent;

    @Column(nullable = false)
    private String source; // "urgent-filter" | "gemini" | "fallback"

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SymptomLog() {}

    public SymptomLog(String message, String reply, boolean urgent, String source) {
        this.message = message;
        this.reply = reply;
        this.urgent = urgent;
        this.source = source;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public boolean isUrgent() { return urgent; }
    public void setUrgent(boolean urgent) { this.urgent = urgent; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
