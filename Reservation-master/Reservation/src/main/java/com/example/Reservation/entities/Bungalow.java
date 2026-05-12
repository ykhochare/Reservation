package com.example.Reservation.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "bungalows")
public class Bungalow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bungalowId;

    private String bungalowName;

    private Double pricePerNight;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bungalow")
    private List<Reservation> reservations;

    @PrePersist
    public void createdAt(){this.createdAt=LocalDateTime.now();}
}
