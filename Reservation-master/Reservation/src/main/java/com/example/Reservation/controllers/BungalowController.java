package com.example.Reservation.controllers;

import com.example.Reservation.dtos.BungalowRequest;
import com.example.Reservation.dtos.BungalowResponse;
import com.example.Reservation.dtos.RevenueResponseDto;
import com.example.Reservation.services.BungalowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bungalows",description = "APIs for managing hotel bungalows")
@RestController
@RequestMapping("/api/bungalows")
public class BungalowController {

    private final BungalowService bungalowService;

    public BungalowController(BungalowService bungalowService) {
        this.bungalowService = bungalowService;
    }

    @Operation(summary = "Add a new bungalow",description = "Creates a new bungalow in the system")
    @PostMapping
    public ResponseEntity<BungalowResponse> addBungalow(@RequestBody BungalowRequest request){
        BungalowResponse response= bungalowService.addBungalow(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update bungalow",description = "Updates existing bungalow details by Id")
    @PutMapping("/{bungalowId}")
    public ResponseEntity<BungalowResponse> updateBungalow(@RequestBody BungalowRequest request,@PathVariable Long bungalowId){
        BungalowResponse response=bungalowService.updateBungalow(bungalowId,request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get bungalow by Id",description = "Returns bungalow details by Id")
    @GetMapping("/{bungalowId}")
    public ResponseEntity<BungalowResponse> getBungalow(@PathVariable Long bungalowId){
        BungalowResponse response=bungalowService.getBungalow(bungalowId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all bungalows",description = "Returns list of all bungalows")
    @GetMapping
    public ResponseEntity<List<BungalowResponse>> getAllBungalows(){
        List<BungalowResponse> bungalowResponses=bungalowService.getAllBungalow();

        return ResponseEntity.ok(bungalowResponses);
    }

    @Operation(summary = "Get revenue by bungalow",description = "Returns total revenue and refunded amount for a specific bungalow")
    @GetMapping("/{bungalowId}/revenue")
    public ResponseEntity<RevenueResponseDto> getBungalowRevenue(@PathVariable Long bungalowId){
        RevenueResponseDto revenueResponse=bungalowService.getRevenueByBungalow(bungalowId);

        return ResponseEntity.ok(revenueResponse);
    }
}
