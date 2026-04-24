package com.example.Reservation.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cancellation_policies")
public class CancellationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_name",nullable = false)
    private String name;

    @Column(name="days_before_checkin_from",nullable = false)
    private Integer daysBeforeCheckInFrom;

    @Column(name = "days_before_checkin_to",nullable = false)
    private Integer daysBeforeCheckInTo;

    @Column(name = "refund_percentage",nullable = false)
    private Double refundPercentage;
}
