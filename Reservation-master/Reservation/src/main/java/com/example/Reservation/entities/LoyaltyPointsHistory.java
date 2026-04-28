package com.example.Reservation.entities;

import com.example.Reservation.enums.PointsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_points_history")
@Getter
@Setter
public class LoyaltyPointsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    private Integer points;

    @Enumerated(EnumType.STRING)
    private PointsType pointsType;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
