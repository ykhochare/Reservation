package com.example.Reservation.events;

import com.example.Reservation.entities.AgentCommission;
import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.CommissionStatus;
import com.example.Reservation.enums.PointsType;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.mappers.ReservationMapper;
import com.example.Reservation.repositories.AgentCommissionRepository;
import com.example.Reservation.repositories.GuestRepository;
import com.example.Reservation.repositories.ReservationRepository;
import com.example.Reservation.services.EmailService;
import com.example.Reservation.services.LoyaltyPointsService;
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

    public ReservationCancelledEventListener(AgentCommissionRepository agentCommissionRepository,
                                             GuestRepository guestRepository,
                                             ReservationRepository reservationRepository,
                                             EmailService emailService,
                                             LoyaltyPointsService loyaltyPointsService) {
        this.agentCommissionRepository = agentCommissionRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
        this.loyaltyPointsService = loyaltyPointsService;
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

        Guest guest=reservation.getGuest();
        int cancelledPoints=loyaltyPointsService.calculateCancellationLoyaltyPoints(reservation);
        int newLoyaltyPoints= Math.max(guest.getLoyaltyPoints()-cancelledPoints,0);
        guest.setLoyaltyPoints(newLoyaltyPoints);

        int newTotalEarned=Math.max(guest.getTotalPointsEarned()-cancelledPoints,0);
        guest.setTotalPointsEarned(newTotalEarned);
        loyaltyPointsService.updateLoyaltyTier(guest);

        guestRepository.save(guest);

        loyaltyPointsService.saveLoyaltyPointsHistory(guest,cancelledPoints, PointsType.EXPIRED);

        reservationRepository.findTopWaitingReservation(reservation.getBungalowId(),"WAITING")
                .ifPresent(waiting->{waiting.setStatus(ReservationStatus.CONFIRMED);
                    reservationRepository.save(waiting);
                    emailService.sendMail(ReservationMapper.toResponseDto(waiting));
                    Guest waitedGuest= waiting.getGuest();
                    int updatedPoints=loyaltyPointsService.calculateConfirmationLoyaltyPoints(waiting);
                    waitedGuest.setLoyaltyPoints(waitedGuest.getLoyaltyPoints()+updatedPoints);
                    waitedGuest.setTotalPointsEarned(waitedGuest.getTotalPointsEarned()+updatedPoints);
                    loyaltyPointsService.updateLoyaltyTier(waitedGuest);
                    guestRepository.save(waitedGuest);
                    loyaltyPointsService.saveLoyaltyPointsHistory(waitedGuest,updatedPoints,PointsType.EARNED);});
    }
}
