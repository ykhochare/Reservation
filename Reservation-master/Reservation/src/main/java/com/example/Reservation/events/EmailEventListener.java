package com.example.Reservation.events;

import com.example.Reservation.config.RabbitMQConfig;
import com.example.Reservation.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailEventListener {

    private final EmailService emailService;

    public EmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailEvent(EmailEvent emailEvent){
        emailService.sendMail(emailEvent.getEmail(), emailEvent.getSubject(), emailEvent.getBody());
    }
}
