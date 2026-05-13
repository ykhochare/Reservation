package com.example.Reservation.services;

import com.example.Reservation.dtos.AvailabilityCheckResponseDto;
import com.example.Reservation.dtos.BungalowAvailabilityRequest;
import com.example.Reservation.dtos.BungalowAvailabilityResponse;
import com.example.Reservation.entities.Bungalow;
import com.example.Reservation.entities.BungalowAvailability;
import com.example.Reservation.enums.AvailabilityStatus;
import com.example.Reservation.exceptions.BungalowNotFoundException;
import com.example.Reservation.mappers.BungalowAvailabilityMapper;
import com.example.Reservation.repositories.BungalowAvailabilityRepository;
import com.example.Reservation.repositories.BungalowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BungalowAvailabilityServiceImpl implements BungalowAvailabilityService{

    private final BungalowAvailabilityRepository bungalowAvailabilityRepository;

    private final BungalowRepository bungalowRepository;

    public BungalowAvailabilityServiceImpl(BungalowAvailabilityRepository bungalowAvailabilityRepository, BungalowRepository bungalowRepository) {
        this.bungalowAvailabilityRepository = bungalowAvailabilityRepository;
        this.bungalowRepository = bungalowRepository;
    }

    @Override
    public BungalowAvailabilityResponse addAvailability(Long bungalowId, BungalowAvailabilityRequest request) {

        Bungalow bungalow=bungalowRepository.findById(bungalowId).orElseThrow(()->new BungalowNotFoundException("Bungalow not found..."));

        BungalowAvailability bungalowAvailability= BungalowAvailabilityMapper.toEntity(request);
        bungalowAvailability.setBungalow(bungalow);

        BungalowAvailability savedAvailability=bungalowAvailabilityRepository.save(bungalowAvailability);

        return BungalowAvailabilityMapper.toResponse(savedAvailability);
    }

    @Override
    public List<BungalowAvailabilityResponse> getAllAvailabilityByBungalow(Long bungalowId) {

        List<BungalowAvailability> availabilities=bungalowAvailabilityRepository.findAllByBungalowBungalowId(bungalowId);

        return availabilities.stream().map(BungalowAvailabilityMapper::toResponse).toList();
    }

    @Override
    public AvailabilityCheckResponseDto checkAvailability(Long bungalowId, LocalDate fromDate, LocalDate toDate) {

        boolean isAvailable=bungalowAvailabilityRepository.isAvailableForDates(bungalowId, fromDate, toDate);

        String message = isAvailable ? "Bungalow is available for the selected dates" : "Bungalow is not available for the selected dates";

        return new AvailabilityCheckResponseDto(isAvailable,message);
    }

    @Override
    public BungalowAvailabilityResponse updateAvailability(AvailabilityStatus availabilityStatus, Long bungalowId, Long availabilityId) {

        BungalowAvailability availability=bungalowAvailabilityRepository.findByIdAndBungalowBungalowId(availabilityId,bungalowId).orElseThrow(()->new RuntimeException("Availability not found..."));
        availability.setStatus(availabilityStatus);

        BungalowAvailability savedAvailability=bungalowAvailabilityRepository.save(availability);

        return BungalowAvailabilityMapper.toResponse(savedAvailability);
    }

    @Override
    public void deleteAvailability(Long bungalowId, Long availabilityId) {

        BungalowAvailability availability=bungalowAvailabilityRepository.findByIdAndBungalowBungalowId(availabilityId,bungalowId).orElseThrow(()->new RuntimeException("Availability not found..."));

        bungalowAvailabilityRepository.delete(availability);

    }
}
