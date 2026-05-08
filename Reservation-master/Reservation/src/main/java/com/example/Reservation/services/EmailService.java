package com.example.Reservation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String email,String subject,String body){

        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("yashkhochare279@gmail.com");

        mailSender.send(message);

    }
}
