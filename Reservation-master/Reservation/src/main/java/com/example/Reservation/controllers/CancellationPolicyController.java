package com.example.Reservation.controllers;

import com.example.Reservation.dtos.CancellationPolicyRequest;
import com.example.Reservation.entities.CancellationPolicy;
import com.example.Reservation.services.CancellationPolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cancellation-policies")
public class CancellationPolicyController {

    private final CancellationPolicyService cancellationPolicyService;

    public CancellationPolicyController(CancellationPolicyService cancellationPolicyService) {
        this.cancellationPolicyService = cancellationPolicyService;
    }

    @PostMapping
    public ResponseEntity<CancellationPolicy> addPolicy(@RequestBody CancellationPolicyRequest request){
        CancellationPolicy policy=cancellationPolicyService.addCancellationPolicy(request);

        return new ResponseEntity<>(policy, HttpStatus.CREATED);
    }

    @PutMapping("/{policyId}")
    public ResponseEntity<CancellationPolicy> updatePolicy(@PathVariable Long policyId,@RequestBody CancellationPolicyRequest request){

        CancellationPolicy policy=cancellationPolicyService.updateCancellationPolicy(policyId, request);

        return ResponseEntity.ok(policy);
    }

    @DeleteMapping("/{policyId}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long policyId){
        cancellationPolicyService.deletePolicy(policyId);

        return ResponseEntity.ok("Policy deleted successfully.");

    }

    @GetMapping("/{policyId}")
    public ResponseEntity<CancellationPolicy> getPolicy(@PathVariable Long policyId){

        CancellationPolicy policy=cancellationPolicyService.getPolicy(policyId);

        return ResponseEntity.ok(policy);
    }

    @GetMapping
    public ResponseEntity<List<CancellationPolicy>> getPolicies(){

        List<CancellationPolicy> policies=cancellationPolicyService.getAllPolicies();

        return ResponseEntity.ok(policies);
    }
}
