package com.example.Reservation.controllers;

import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.services.ExcelService;
import com.example.Reservation.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Excel",description = "APIs for managing importing and exporting reservations via Excel")
@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private final ReservationService reservationService;

    private  final ExcelService excelService;

    public ExcelController(ReservationService reservationService, ExcelService excelService) {
        this.reservationService = reservationService;
        this.excelService = excelService;
    }

    @Operation(summary = "Download reservations as Excel",description = "Exports reservations to Excel file with optional filters by status ,arrival and departure date and bungalow")
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

    @Operation(summary = "Upload reservations from Excel",description = "Imports reservations from Excel file.Optionally link to a travel agent by passing agentId")
    @PostMapping("/reservation/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(required = false) Long agentId) {

        excelService.uploadExcel(file,agentId);

        return ResponseEntity.ok("Excel uploaded successfully!");
    }
}
