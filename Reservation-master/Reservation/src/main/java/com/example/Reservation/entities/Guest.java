package com.example.Reservation.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "guests")
@Getter
@Setter
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;

    private String guestName;

    private String guestEmail;

    private String phone;

    private Integer loyaltyPoints;   // total points available to redeem

    private LocalDateTime registeredAt;

    @OneToMany(mappedBy = "guest")
    private List<Reservation> reservations;

    @PrePersist
    public void registeredAt(){this.registeredAt=LocalDateTime.now();}

}
