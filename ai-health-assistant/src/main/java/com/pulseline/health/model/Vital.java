package com.pulseline.health.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vitals")
public class Vital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** bp | hr | temp | spo2 */
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String unit;

    /** whether the value fell outside the typical reference range */
    @Column(nullable = false)
    private boolean flagged;

    @Column(nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    public Vital() {}

    public Vital(String type, Double value, String unit, boolean flagged) {
        this.type = type;
        this.value = value;
        this.unit = unit;
        this.flagged = flagged;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
