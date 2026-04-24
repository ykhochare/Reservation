package com.example.Reservation.specifications;

import com.example.Reservation.entities.Cancellation;
import com.example.Reservation.enums.RefundStatus;
import org.springframework.data.jpa.domain.Specification;

public class CancellationSpecification {

    public static Specification<Cancellation> hasStatus(RefundStatus status){

        return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("refundStatus"),status);
    }

    public static Specification<Cancellation> hasPolicyId(Long policyId){

        return (root,query,builder)->
                    builder.equal(root.get("policy").get("id"),policyId);
    }
}
