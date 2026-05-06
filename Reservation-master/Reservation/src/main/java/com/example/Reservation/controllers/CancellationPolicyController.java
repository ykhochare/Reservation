package com.example.Reservation.controllers;

import com.example.Reservation.dtos.CancellationPolicyRequest;
import com.example.Reservation.entities.CancellationPolicy;
import com.example.Reservation.services.CancellationPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cancellation Policies",description = "APIs for managing cancellation policies")
@RestController
@RequestMapping("/api/cancellation-policies")
public class CancellationPolicyController {

    private final CancellationPolicyService cancellationPolicyService;

    public CancellationPolicyController(CancellationPolicyService cancellationPolicyService) {
        this.cancellationPolicyService = cancellationPolicyService;
    }

    @Operation(summary = "Create a new cancellation policy",description = "Creates a new cancellation policy with refund percentage based on days before arrival")
    @PostMapping
    public ResponseEntity<CancellationPolicy> addPolicy(@RequestBody CancellationPolicyRequest request){
        CancellationPolicy policy=cancellationPolicyService.addCancellationPolicy(request);

        return new ResponseEntity<>(policy, HttpStatus.CREATED);
    }

    @Operation(summary = "Update cancellation policy",description = "Updates an existing cancellation policy by Id")
    @PutMapping("/{policyId}")
    public ResponseEntity<CancellationPolicy> updatePolicy(@PathVariable Long policyId,@RequestBody CancellationPolicyRequest request){

        CancellationPolicy policy=cancellationPolicyService.updateCancellationPolicy(policyId, request);

        return ResponseEntity.ok(policy);
    }

    @Operation(summary = "Delete cancellation policy",description = "Deletes a cancellation policy by Id")
    @DeleteMapping("/{policyId}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long policyId){
        cancellationPolicyService.deletePolicy(policyId);

        return ResponseEntity.ok("Policy deleted successfully.");

    }

    @Operation(summary = "Get cancellation policy by Id",description = "Returns a single cancellation policy by Id")
    @GetMapping("/{policyId}")
    public ResponseEntity<CancellationPolicy> getPolicy(@PathVariable Long policyId){

        CancellationPolicy policy=cancellationPolicyService.getPolicy(policyId);

        return ResponseEntity.ok(policy);
    }

    @Operation(summary = "Get all cancellation policies",description = "Returns all cancellation policies defined in the system")
    @GetMapping
    public ResponseEntity<List<CancellationPolicy>> getPolicies(){

        List<CancellationPolicy> policies=cancellationPolicyService.getAllPolicies();

        return ResponseEntity.ok(policies);
    }
}
