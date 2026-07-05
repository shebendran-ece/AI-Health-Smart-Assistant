package com.pulseline.health.dto;

import jakarta.validation.constraints.NotBlank;

public class SymptomRequest {

    @NotBlank
    private String message;

    public SymptomRequest() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
