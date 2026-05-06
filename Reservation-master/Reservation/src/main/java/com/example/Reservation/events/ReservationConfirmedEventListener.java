package com.example.Reservation.events;

import com.example.Reservation.config.RabbitMQConfig;
import com.example.Reservation.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReservationConfirmedEventListener {

    private final EmailService emailService;

    public ReservationConfirmedEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.CONFIRMATION_QUEUE)
    public void handleConfirmation(ReservationConfirmedEvent event) {
        emailService.sendMail(event.getReservation());
    }
}
