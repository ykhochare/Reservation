package com.example.Reservation.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class TravelAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agentId;

    private String agentName;

    private String agentEmail;

    private Double commissionRate;

    @OneToMany(mappedBy = "travelAgent")
    private List<Reservation> reservations;
}
