package com.example.Reservation.controllers;

import com.example.Reservation.dtos.GuestRequest;
import com.example.Reservation.dtos.GuestResponse;
import com.example.Reservation.services.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Guest", description = "Api's for managing hotel guests.")
@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @Operation(summary = "Register a new guest",description = "Register a new guest and award 50 loyalty points as registration bonus")
    @PostMapping("/register")
    public ResponseEntity<GuestResponse> register(@RequestBody GuestRequest guestRequest){
        GuestResponse guestResponse=guestService.registerGuest(guestRequest);

        return new ResponseEntity<>(guestResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Get guest by Id",description = "Returns guest details including loyalty points and tier")
    @GetMapping("/{guestId}")
    public ResponseEntity<GuestResponse> getGuest(@PathVariable Long guestId){
        GuestResponse guestResponse=guestService.getGuestById(guestId);

        return ResponseEntity.ok(guestResponse);
    }

    @Operation(summary = "Update guest details",description = "Update existing guest details")
    @PutMapping("/{guestId}")
    public ResponseEntity<GuestResponse> updateGuest(@PathVariable Long guestId,@RequestBody GuestRequest guestRequest){
        GuestResponse guestResponse=guestService.editGuest(guestId,guestRequest);

        return ResponseEntity.ok(guestResponse);
    }

    @Operation(summary = "Get guest by email",description = "Returns guest details by email address")
    @GetMapping("/email")
    public ResponseEntity<GuestResponse> getGuestByEmail(@RequestParam String email){
        GuestResponse guestResponse=guestService.getByEmail(email);

        return ResponseEntity.ok(guestResponse);
    }

}
