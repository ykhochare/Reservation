package com.example.Reservation.services;

import com.example.Reservation.dtos.CancellationPolicyRequest;
import com.example.Reservation.entities.CancellationPolicy;

import java.util.List;

public interface CancellationPolicyService {

    CancellationPolicy addCancellationPolicy(CancellationPolicyRequest request);

    CancellationPolicy updateCancellationPolicy(Long id,CancellationPolicyRequest request);

    void deletePolicy(Long id);

    CancellationPolicy getPolicy(Long id);

    List<CancellationPolicy> getAllPolicies();
}
