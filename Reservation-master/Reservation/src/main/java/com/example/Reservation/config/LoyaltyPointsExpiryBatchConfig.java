package com.example.Reservation.config;

import com.example.Reservation.entities.Guest;
import com.example.Reservation.entities.LoyaltyPointsHistory;
import com.example.Reservation.enums.PointsType;
import com.example.Reservation.repositories.GuestRepository;
import com.example.Reservation.repositories.LoyaltyPointsHistoryRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Configuration
public class LoyaltyPointsExpiryBatchConfig {

    @Bean
    public RepositoryItemReader<LoyaltyPointsHistory> loyaltyPointsExpiryReader(
            LoyaltyPointsHistoryRepository repository) {

        RepositoryItemReader<LoyaltyPointsHistory> reader = new RepositoryItemReader<>();
        reader.setRepository(repository);
        reader.setMethodName("findByPointsTypeAndCreatedAtBefore");
        reader.setArguments(List.of(PointsType.EARNED, LocalDateTime.now().minusYears(1)));
        reader.setPageSize(10);
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public ItemProcessor<LoyaltyPointsHistory, LoyaltyPointsHistory> loyaltyPointsExpiryProcessor(
            GuestRepository guestRepository) {

        return history -> {
            Guest guest = history.getGuest();
            int newPoints = Math.max(guest.getLoyaltyPoints() - history.getPoints(), 0);
            guest.setLoyaltyPoints(newPoints);
            guestRepository.save(guest);

            history.setPointsType(PointsType.EXPIRED);
            return history;
        };
    }

    @Bean
    public RepositoryItemWriter<LoyaltyPointsHistory> loyaltyPointsExpiryWriter(
            LoyaltyPointsHistoryRepository repository) {

        RepositoryItemWriter<LoyaltyPointsHistory> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step loyaltyPointsExpiryStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager,
                                        RepositoryItemReader<LoyaltyPointsHistory> loyaltyPointsExpiryReader,
                                        ItemProcessor<LoyaltyPointsHistory, LoyaltyPointsHistory> loyaltyPointsExpiryProcessor,
                                        RepositoryItemWriter<LoyaltyPointsHistory> loyaltyPointsExpiryWriter) {

        return new StepBuilder("loyaltyPointsExpiryStep", jobRepository)
                .<LoyaltyPointsHistory, LoyaltyPointsHistory>chunk(10, transactionManager)
                .reader(loyaltyPointsExpiryReader)
                .processor(loyaltyPointsExpiryProcessor)
                .writer(loyaltyPointsExpiryWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Job loyaltyPointsExpiryJob(JobRepository jobRepository, Step loyaltyPointsExpiryStep) {
        return new JobBuilder("loyaltyPointsExpiryJob", jobRepository)
                .start(loyaltyPointsExpiryStep)
                .build();
    }
}
