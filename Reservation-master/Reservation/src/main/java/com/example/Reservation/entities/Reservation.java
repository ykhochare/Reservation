package com.example.Reservation.entities;

import com.example.Reservation.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bungalow_id", nullable = false)
    private Long bungalowId;

    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    // One-to-One with Cancellation
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Cancellation cancellation;

    // One-to-Many with payments
    @OneToMany(mappedBy = "reservation",cascade = CascadeType.ALL)
    private List<Payment> payments;

    // Many-to-One with Guest
    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @PrePersist
    public void createdAt(){
        this.createdAt=LocalDateTime.now();
    }
}
