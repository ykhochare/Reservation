package com.example.Reservation.services;

import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.repositories.ReservationRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelService {

    private final ReservationRepository reservationRepository;

    public ExcelService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ByteArrayInputStream generateExcel(List<ReservationResponse> reservations) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reservations");

            // Header Row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Guest Name");
            headerRow.createCell(2).setCellValue("Guest Email");
            headerRow.createCell(3).setCellValue("Bangalow Number");
            headerRow.createCell(4).setCellValue("Arrival Date");
            headerRow.createCell(5).setCellValue("Departure Date");
            headerRow.createCell(6).setCellValue("Total Amount");
            headerRow.createCell(7).setCellValue("Status");

            int rowIdx = 1;

            for (ReservationResponse reservation : reservations) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(reservation.getId());
                row.createCell(1).setCellValue(reservation.getGuestName());
                row.createCell(2).setCellValue(reservation.getGuestEmail());
                row.createCell(3).setCellValue(reservation.getBungalowId());
                row.createCell(4).setCellValue(reservation.getArrivalDate().toString());
                row.createCell(5).setCellValue(reservation.getDepartureDate().toString());
                row.createCell(6).setCellValue(reservation.getTotalAmount());
                row.createCell(7).setCellValue(String.valueOf(reservation.getStatus()));
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }


    public void uploadExcel(MultipartFile file) {

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header
            if (rows.hasNext()) rows.next();

            List<Reservation> reservations = new ArrayList<>();

            while (rows.hasNext()) {
                Row row = rows.next();

                Reservation reservation = new Reservation();

//                // Name
//                reservation.setGuestName(getStringValue(row.getCell(0)));
//
//                // Email
//                reservation.setGuestEmail(getStringValue(row.getCell(1)));

                // BungalowId
                reservation.setBungalowId(getLongValue(row.getCell(2)));

                // ArrivalDate
                reservation.setArrivalDate(getLocalDate(row.getCell(3)));

                // DepartureDate
                reservation.setDepartureDate(getLocalDate(row.getCell(4)));

                // TotalAmount
                reservation.setTotalAmount(getDoubleValue(row.getCell(5)));

                // Enum
                reservation.setStatus(getStatus(row.getCell(6)));

                reservations.add(reservation);
            }

            reservationRepository.saveAll(reservations);

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload Excel", e);
        }
    }

    private LocalDate getLocalDate(Cell cell) {
        if (cell == null) return null;

        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        return null;
    }

    private ReservationStatus getStatus(Cell cell){

        if(cell==null)
            return null;

        String value=cell.getStringCellValue();

        return ReservationStatus.valueOf(value.trim().toUpperCase());
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        return cell.toString().trim();
    }

    private long getLongValue(Cell cell){
        if(cell==null) return 0;

        return (long)cell.getNumericCellValue();
    }

    private double getDoubleValue(Cell cell){
        if(cell==null) return 0.0;

        return cell.getNumericCellValue();
    }

}
