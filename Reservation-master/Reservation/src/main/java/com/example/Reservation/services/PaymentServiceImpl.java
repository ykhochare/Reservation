package com.example.Reservation.services;

import com.example.Reservation.config.RabbitMQConfig;
import com.example.Reservation.dtos.PaymentResponse;
import com.example.Reservation.dtos.PaymentSuccessResponse;
import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.LoyaltyPointsHistory;
import com.example.Reservation.entities.Payment;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.PointsType;
import com.example.Reservation.events.EmailEvent;
import com.example.Reservation.exceptions.ReservationNotFoundException;
import com.example.Reservation.mappers.PaymentMapper;
import com.example.Reservation.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;

    private final ReservationRepository reservationRepository;

    private final GuestRepository guestRepository;

    private final LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository;

    private final RabbitTemplate rabbitTemplate;

    public PaymentServiceImpl(PaymentRepository paymentRepository, ReservationRepository reservationRepository, GuestRepository guestRepository, LoyaltyPointsHistoryRepository loyaltyPointsHistoryRepository, RabbitTemplate rabbitTemplate) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.loyaltyPointsHistoryRepository = loyaltyPointsHistoryRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public PaymentSuccessResponse pay(Long reservationId, Double amount,Integer pointsUseTo) {

        Reservation reservation=reservationRepository.findById(reservationId).orElseThrow(()->new ReservationNotFoundException("Reservation not found..."));

        if(pointsUseTo>reservation.getGuest().getLoyaltyPoints())
            throw new RuntimeException("You do not have enough loyalty points.");

        List<Payment> payments=reservation.getPayments();

        double paidAmount=payments.stream().mapToDouble(Payment::getAmount).sum();

        double totalPaidAmount=paidAmount+amount;

        if(totalPaidAmount>reservation.getTotalAmount())
            throw new RuntimeException("Payment exceeds");

        Payment payment=new Payment();

        payment.setAmount(amount);
        payment.setReservation(reservation);
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);

        Guest guest= reservation.getGuest();
        int updatedLoyaltyPoints=guest.getLoyaltyPoints()-pointsUseTo;
        guest.setLoyaltyPoints(updatedLoyaltyPoints);
        guestRepository.save(guest);

        if(pointsUseTo != 0)
            saveLoyaltyPointsHistory(guest,pointsUseTo);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                new EmailEvent(guest.getGuestEmail(), "Payment Successful!",paymentSuccessEmail(guest.getGuestName(), totalPaidAmount,updatedLoyaltyPoints))
        );

        return new PaymentSuccessResponse(totalPaidAmount,updatedLoyaltyPoints);

    }

    @Override
    public List<PaymentResponse> getAllPayments(Long reservationId) {

        List<Payment> payments=paymentRepository.getByReservation_Id(reservationId);

        return payments.stream().map(PaymentMapper::toResponseDto).toList();
    }

    private void saveLoyaltyPointsHistory(Guest guest, Integer points){

        LoyaltyPointsHistory history=new LoyaltyPointsHistory();
        history.setGuest(guest);
        history.setPoints(points);
        history.setPointsType(PointsType.REDEEMED);
        loyaltyPointsHistoryRepository.save(history);
    }

    private String paymentSuccessEmail(String guestName,double amountPaid,int remainingPoints){
        return "Dear " + guestName + ",\n\n" +
                "Your payment has been received successfully!\n\n" +
                "Payment Details:\n" +
                "Amount Paid       : ₹" + amountPaid + "\n" +
                "Remaining Points  : " + remainingPoints + "\n\n" +
                "Thank you for choosing Silver Heavens Resort!\n\n" +
                "Regards,\n" +
                "Silver Heavens Resort";
    }
}
