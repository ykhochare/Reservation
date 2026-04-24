package com.example.Reservation.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}
