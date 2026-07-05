package com.pulseline.health.dto;

public class SymptomResponse {

    private String reply;
    private boolean urgent;
    private String source; // "urgent-filter" | "gemini" | "fallback"

    public SymptomResponse() {}

    public SymptomResponse(String reply, boolean urgent, String source) {
        this.reply = reply;
        this.urgent = urgent;
        this.source = source;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public boolean isUrgent() { return urgent; }
    public void setUrgent(boolean urgent) { this.urgent = urgent; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
