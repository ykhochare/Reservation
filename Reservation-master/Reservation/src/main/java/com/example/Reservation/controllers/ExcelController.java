package com.example.Reservation.controllers;

import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.services.ExcelService;
import com.example.Reservation.services.ReservationService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private final ReservationService reservationService;

    private  final ExcelService excelService;

    public ExcelController(ReservationService reservationService, ExcelService excelService) {
        this.reservationService = reservationService;
        this.excelService = excelService;
    }

    @GetMapping("/reservation/download")
    public ResponseEntity<InputStreamResource> downloadReservationExcel(@RequestParam(required = false)ReservationStatus status,
                                                                        @RequestParam(required = false)LocalDate arrival,
                                                                        @RequestParam(required = false)LocalDate departure,
                                                                        @RequestParam(required = false)Long bungalowId){
        List<ReservationResponse> reservations=reservationService.getAllReservations(status, arrival, departure, bungalowId);

        ByteArrayInputStream in=excelService.generateExcel(reservations);

        HttpHeaders headers=new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reservations.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }

    @PostMapping("/reservation/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(required = false) Long agentId) {

        excelService.uploadExcel(file,agentId);

        return ResponseEntity.ok("Excel uploaded successfully!");
    }
}
