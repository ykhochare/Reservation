package com.example.Reservation.services;

import com.example.Reservation.dtos.BungalowRequest;
import com.example.Reservation.dtos.BungalowResponse;
import com.example.Reservation.dtos.RevenueResponseDto;
import com.example.Reservation.entities.Bungalow;
import com.example.Reservation.exceptions.BungalowNotFoundException;
import com.example.Reservation.mappers.BungalowMapper;
import com.example.Reservation.repositories.BungalowRepository;
import com.example.Reservation.repositories.CancellationRepository;
import com.example.Reservation.repositories.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BungalowServiceImpl implements BungalowService{

    private final BungalowRepository bungalowRepository;

    private final PaymentRepository paymentRepository;

    private final CancellationRepository cancellationRepository;

    public BungalowServiceImpl(BungalowRepository bungalowRepository, PaymentRepository paymentRepository, CancellationRepository cancellationRepository) {
        this.bungalowRepository = bungalowRepository;
        this.paymentRepository = paymentRepository;
        this.cancellationRepository = cancellationRepository;
    }

    @Override
    public BungalowResponse addBungalow(BungalowRequest bungalowRequest) {

        Bungalow bungalow= BungalowMapper.toEntity(bungalowRequest);

        Bungalow savedBungalow=bungalowRepository.save(bungalow);

        return BungalowMapper.toResponseDto(savedBungalow);
    }

    @Override
    public BungalowResponse updateBungalow(Long bungalowId, BungalowRequest request) {

        Bungalow bungalow=bungalowRepository.findById(bungalowId).orElseThrow(()->new BungalowNotFoundException("Bungalow not found..."));

        bungalow.setBungalowName(request.getBungalowName());
        bungalow.setPricePerNight(request.getPricePerNight());
        Bungalow savedBungalow=bungalowRepository.save(bungalow);

        return BungalowMapper.toResponseDto(savedBungalow);
    }

    @Override
    public BungalowResponse getBungalow(Long bungalowId) {
        Bungalow bungalow=bungalowRepository.findById(bungalowId).orElseThrow(()->new BungalowNotFoundException("Bungalow not found..."));

        return BungalowMapper.toResponseDto(bungalow);
    }

    @Override
    public List<BungalowResponse> getAllBungalow() {

        List<Bungalow> bungalows=bungalowRepository.findAll();

        return bungalows.stream().map(BungalowMapper::toResponseDto).toList();
    }

    @Override
    public RevenueResponseDto getRevenueByBungalow(Long bungalowId) {
        Double totalRevenue=paymentRepository.calculateRevenueByBungalow(bungalowId);

        Double totalRefunds= cancellationRepository.calculateRefundByBungalow(bungalowId);

        double netRevenue=totalRevenue-totalRefunds;
        return new RevenueResponseDto(bungalowId,totalRevenue,totalRefunds,netRevenue);
    }
}
