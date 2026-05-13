package com.example.Reservation.events;

import com.example.Reservation.entities.AgentCommission;
import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.CommissionStatus;
import com.example.Reservation.enums.PointsType;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.exceptions.GuestNotFoundException;
import com.example.Reservation.repositories.AgentCommissionRepository;
import com.example.Reservation.repositories.GuestRepository;
import com.example.Reservation.repositories.ReservationRepository;
import com.example.Reservation.services.EmailService;
import com.example.Reservation.services.LoyaltyPointsService;
import com.example.Reservation.services.ReservationServiceImpl;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ReservationCancelledEventListener {

    private final AgentCommissionRepository agentCommissionRepository;

    private final GuestRepository guestRepository;

    private final ReservationRepository reservationRepository;

    private final EmailService emailService;

    private final LoyaltyPointsService loyaltyPointsService;

    private final ReservationServiceImpl reservationService;

    public ReservationCancelledEventListener(AgentCommissionRepository agentCommissionRepository,
                                             GuestRepository guestRepository,
                                             ReservationRepository reservationRepository,
                                             EmailService emailService,
                                             LoyaltyPointsService loyaltyPointsService,
                                             ReservationServiceImpl reservationService) {
        this.agentCommissionRepository = agentCommissionRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
        this.loyaltyPointsService = loyaltyPointsService;
        this.reservationService = reservationService;
    }

    @Async
    @EventListener
    public void handleCancellation(ReservationCancelledEvent event){

        Reservation reservation=event.getReservation();

        AgentCommission agentCommission=reservation.getAgentCommission();
        if(agentCommission!=null) {

            if(agentCommission.getStatus()== CommissionStatus.PAID)
                agentCommission.setRecoveryRequired(true);
            agentCommission.setStatus(CommissionStatus.REVERSED);
            agentCommissionRepository.save(agentCommission);
        }

        Guest guest=guestRepository.findById(reservation.getGuest().getGuestId()).orElseThrow(()->new GuestNotFoundException("Guest not found..."));
        int cancelledPoints=loyaltyPointsService.calculateCancellationLoyaltyPoints(reservation);
        int newLoyaltyPoints= Math.max(guest.getLoyaltyPoints()-cancelledPoints,0);
        guest.setLoyaltyPoints(newLoyaltyPoints);

        int newTotalEarned=Math.max(guest.getTotalPointsEarned()-cancelledPoints,0);
        guest.setTotalPointsEarned(newTotalEarned);
        loyaltyPointsService.updateLoyaltyTier(guest);

        guestRepository.save(guest);

        loyaltyPointsService.saveLoyaltyPointsHistory(guest,cancelledPoints, PointsType.EXPIRED);

        reservationRepository.findTopWaitingReservation(reservation.getBungalow().getBungalowId(),"WAITING")
                .ifPresent(waiting->{waiting.setStatus(ReservationStatus.CONFIRMED);
                    reservationRepository.save(waiting);
                    reservationService.split(waiting.getBungalow(),waiting.getArrivalDate(),waiting.getDepartureDate());
                    Guest waitedGuest= guestRepository.findById(waiting.getGuest().getGuestId()).orElseThrow(()->new GuestNotFoundException("Guest not found..."));
                    emailService.sendMail(waitedGuest.getGuestEmail(),"Reservation Confirmed",confirmationEmailBody(waiting));
                    int updatedPoints=loyaltyPointsService.calculateConfirmationLoyaltyPoints(waiting);
                    waitedGuest.setLoyaltyPoints(waitedGuest.getLoyaltyPoints()+updatedPoints);
                    waitedGuest.setTotalPointsEarned(waitedGuest.getTotalPointsEarned()+updatedPoints);
                    loyaltyPointsService.updateLoyaltyTier(waitedGuest);
                    guestRepository.save(waitedGuest);
                    loyaltyPointsService.saveLoyaltyPointsHistory(waitedGuest,updatedPoints,PointsType.EARNED);});
    }

    private String confirmationEmailBody(Reservation reservation){
        return "Dear " + reservation.getGuest().getGuestName() + ",\n\n" +
                "Your reservation has been confirmed at Silver Heavens Resort!\n\n" +
                "Reservation Details:\n" +
                "Bungalow ID  : " + reservation.getBungalow().getBungalowId() + "\n" +
                "Arrival Date : " + reservation.getArrivalDate() + "\n" +
                "Departure Date: " + reservation.getDepartureDate() + "\n" +
                "Total Amount : ₹" + reservation.getTotalAmount() + "\n\n" +
                "We look forward to welcoming you!\n\n" +
                "Regards,\n" +
                "Silver Heavens Resort";
    }
}
