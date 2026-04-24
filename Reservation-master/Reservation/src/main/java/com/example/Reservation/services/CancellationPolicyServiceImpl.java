package com.example.Reservation.services;

import com.example.Reservation.dtos.CancellationPolicyRequest;
import com.example.Reservation.entities.CancellationPolicy;
import com.example.Reservation.exceptions.CancellationPolicyNotFoundException;
import com.example.Reservation.repositories.CancellationPolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CancellationPolicyServiceImpl implements CancellationPolicyService{

    private final CancellationPolicyRepository cancellationPolicyRepository;

    public CancellationPolicyServiceImpl(CancellationPolicyRepository cancellationPolicyRepository) {
        this.cancellationPolicyRepository = cancellationPolicyRepository;
    }

    @Override
    public CancellationPolicy addCancellationPolicy(CancellationPolicyRequest request) {

        if(cancellationPolicyRepository.existsOverlappingPolicy(request.getDaysBeforeCheckInFrom(), request.getDaysBeforeCheckInTo()))
            throw new RuntimeException("Invalid policy range: overlaps with an existing cancellation policy");

        CancellationPolicy policy=new CancellationPolicy();
        policy.setName(request.getName());
        policy.setDaysBeforeCheckInFrom(request.getDaysBeforeCheckInFrom());
        policy.setDaysBeforeCheckInTo(request.getDaysBeforeCheckInTo());
        policy.setRefundPercentage(request.getRefundPercentage());

        return cancellationPolicyRepository.save(policy);
    }

    @Override
    public CancellationPolicy updateCancellationPolicy(Long id, CancellationPolicyRequest request) {
        CancellationPolicy cancellationPolicy=cancellationPolicyRepository.findById(id).orElseThrow(()->new CancellationPolicyNotFoundException("Policy is not available"));
        cancellationPolicy.setName(request.getName());
        cancellationPolicy.setDaysBeforeCheckInFrom(request.getDaysBeforeCheckInFrom());
        cancellationPolicy.setDaysBeforeCheckInTo(request.getDaysBeforeCheckInTo());
        cancellationPolicy.setRefundPercentage(request.getRefundPercentage());
        return cancellationPolicyRepository.save(cancellationPolicy);
    }

    @Override
    public void deletePolicy(Long id) {
        cancellationPolicyRepository.deleteById(id);
    }

    @Override
    public CancellationPolicy getPolicy(Long id) {
        return cancellationPolicyRepository.findById(id).orElseThrow(()->new CancellationPolicyNotFoundException("Policy is not available"));
    }

    @Override
    public List<CancellationPolicy> getAllPolicies() {
        return cancellationPolicyRepository.findAll();
    }
}
