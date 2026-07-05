package com.pulseline.health.dto;

import jakarta.validation.constraints.NotBlank;

public class MedicationRequest {

    @NotBlank
    private String name;

    private String time;

    public MedicationRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
