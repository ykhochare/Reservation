package com.example.Reservation.controllers;

import com.example.Reservation.dtos.GuestRequest;
import com.example.Reservation.dtos.GuestResponse;
import com.example.Reservation.services.GuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PostMapping("/register")
    public ResponseEntity<GuestResponse> register(@RequestBody GuestRequest guestRequest){
        GuestResponse guestResponse=guestService.registerGuest(guestRequest);

        return new ResponseEntity<>(guestResponse, HttpStatus.CREATED);
    }

    @GetMapping("{guestId}")
    public ResponseEntity<GuestResponse> getGuest(@PathVariable Long guestId){
        GuestResponse guestResponse=guestService.getGuestById(guestId);

        return ResponseEntity.ok(guestResponse);
    }

}
