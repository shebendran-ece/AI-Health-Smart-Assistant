package com.pulseline.health.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "water_logs")
public class WaterLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date = LocalDate.now();

    @Column(nullable = false)
    private int cups = 0;

    public WaterLog() {}

    public WaterLog(LocalDate date, int cups) {
        this.date = date;
        this.cups = cups;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getCups() { return cups; }
    public void setCups(int cups) { this.cups = cups; }
}
