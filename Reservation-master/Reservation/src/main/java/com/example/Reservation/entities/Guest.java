package com.example.Reservation.entities;

import com.example.Reservation.enums.LoyaltyTier;
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

    private Integer loyaltyPoints;

    private Integer totalPointsEarned;

    @Enumerated(EnumType.STRING)
    private LoyaltyTier loyaltyTier;

    private LocalDateTime registeredAt;

    @Version
    private Long version;

    @OneToMany(mappedBy = "guest")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
    private List<LoyaltyPointsHistory> loyaltyPointsHistory;

    @PrePersist
    public void registeredAt(){this.registeredAt=LocalDateTime.now();}

}
