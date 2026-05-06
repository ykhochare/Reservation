package com.example.Reservation.services;

import com.example.Reservation.dtos.ReservationResponse;
import com.example.Reservation.entities.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(ReservationResponse reservation){

        String body = "Hello " + reservation.getGuestName() + ",\n\n"
                + "Your reservation is successfully confirmed.\n\n"
                + "Reservation Details:\n"
                + "Reservation ID: " + reservation.getId() + "\n"
                + "Bungalow Number: " + reservation.getBungalowId() + "\n"
                + "Total Amount: ₹" + reservation.getTotalAmount() + "\n\n"
                + "Thank you for choosing our service!";

        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(reservation.getGuestEmail());
        message.setSubject("Confirm Reservation");
        message.setText(body);
        message.setFrom("yashkhochare279@gmail.com");

        mailSender.send(message);

    }
}
