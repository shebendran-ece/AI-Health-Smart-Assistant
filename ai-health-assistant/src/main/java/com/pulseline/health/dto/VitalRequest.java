package com.pulseline.health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VitalRequest {

    @NotBlank
    private String type; // bp | hr | temp | spo2

    @NotNull
    private Double value;

    public VitalRequest() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
}
