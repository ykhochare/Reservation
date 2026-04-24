package com.example.Reservation.specifications;

import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.ReservationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ReservationSpecification {

    public static Specification<Reservation> hasStatus(ReservationStatus status){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status));
    }

    public static Specification<Reservation> hasArrival(LocalDate arrivalDate){
        return (root,query,criteriaBuilder)->
                    criteriaBuilder.equal(root.get("arrivalDate"),arrivalDate);
    }

    public static Specification<Reservation> hasDeparture(LocalDate departureDate){
        return (root,query,criteriaBuilder)->
                    criteriaBuilder.equal(root.get("departureDate"),departureDate);
    }

    public static Specification<Reservation> hasBungalow(Long id){
        return (root,query,criteriaBuilder)->
                criteriaBuilder.equal(root.get("bungalowId"),id);
    }
}
