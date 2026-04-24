package com.example.Reservation.entities;

import com.example.Reservation.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="cancellations")
public class Cancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cancelled_at", nullable = false)
    private LocalDateTime cancelledAt;

    @Column(name = "days_before_checkin", nullable = false)
    private Integer daysBeforeCheckIn;

    @Column(name = "refund_amount", nullable = false)
    private Double refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    private RefundStatus refundStatus;

    private String reason;

    // One-to-One with Reservation
    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // Many-to-One with CancellationPolicy
    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private CancellationPolicy policy;

    @PrePersist
    public void cancelledAt(){
        this.cancelledAt=LocalDateTime.now();
    }
}
