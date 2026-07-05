package com.pulseline.health.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medications")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private boolean taken = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Medication() {}

    public Medication(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isTaken() { return taken; }
    public void setTaken(boolean taken) { this.taken = taken; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
