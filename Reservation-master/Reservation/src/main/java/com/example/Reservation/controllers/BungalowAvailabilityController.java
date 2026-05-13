package com.example.Reservation.controllers;

import com.example.Reservation.dtos.AvailabilityCheckResponseDto;
import com.example.Reservation.dtos.BungalowAvailabilityRequest;
import com.example.Reservation.dtos.BungalowAvailabilityResponse;
import com.example.Reservation.enums.AvailabilityStatus;
import com.example.Reservation.services.BungalowAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Bungalow Availability", description = "Manage availability records for bungalows")
@RestController
@RequestMapping("/api/bungalows/{bungalowId}/availability")
public class BungalowAvailabilityController {

    private final BungalowAvailabilityService availabilityService;

    public BungalowAvailabilityController(BungalowAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @Operation(summary = "Add availability", description = "Staff adds a date range with availability status for a bungalow")
    @PostMapping
    public ResponseEntity<BungalowAvailabilityResponse> addAvailability(@PathVariable Long bungalowId, @RequestBody BungalowAvailabilityRequest request){
        BungalowAvailabilityResponse response=availabilityService.addAvailability(bungalowId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update availability status", description = "Update status of an availability record e.g. set to MAINTENANCE")
    @PatchMapping("/{availabilityId}")
    public ResponseEntity<BungalowAvailabilityResponse> updateAvailability(@PathVariable Long bungalowId,
                                                                           @PathVariable Long availabilityId,
                                                                           @RequestParam AvailabilityStatus status){

        BungalowAvailabilityResponse response=availabilityService.updateAvailability(status,bungalowId,availabilityId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all availability", description = "Get all availability records for a bungalow")
    @GetMapping
    public ResponseEntity<List<BungalowAvailabilityResponse>> getAllAvailability(@PathVariable Long bungalowId){
        List<BungalowAvailabilityResponse> availabilityResponses=availabilityService.getAllAvailabilityByBungalow(bungalowId);

        return ResponseEntity.ok(availabilityResponses);
    }

    @Operation(summary = "Check availability", description = "Check if bungalow is available for given date range")
    @GetMapping("/check")
    public ResponseEntity<AvailabilityCheckResponseDto> checkAvailability(@PathVariable Long bungalowId,
                                                                          @RequestParam LocalDate fromDate,
                                                                          @RequestParam LocalDate toDate){

        AvailabilityCheckResponseDto availabilityCheck=availabilityService.checkAvailability(bungalowId, fromDate, toDate);

        return ResponseEntity.ok(availabilityCheck);
    }

    @Operation(summary = "Delete availability", description = "Delete an availability record for a bungalow")
    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<String> deleteAvailability(@PathVariable Long bungalowId,@PathVariable Long availabilityId){

        availabilityService.deleteAvailability(bungalowId, availabilityId);

        return ResponseEntity.ok("Availability entry deleted successfully");
    }
}
