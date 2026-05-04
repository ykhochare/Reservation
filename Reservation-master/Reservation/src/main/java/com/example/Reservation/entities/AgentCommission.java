package com.example.Reservation.entities;

import com.example.Reservation.enums.CommissionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AgentCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private TravelAgent travelAgent;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private Boolean recoveryRequired = false;

    private Double commissionAmount;

    @Enumerated(EnumType.STRING)
    private CommissionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
