package com.example.Reservation.config;

import com.example.Reservation.entities.Cancellation;
import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.RefundStatus;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.repositories.CancellationRepository;
import com.example.Reservation.repositories.ReservationRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class ReservationBatchConfig {

    @Bean
    public RepositoryItemReader<Reservation> reservationReader(ReservationRepository repository) {

        RepositoryItemReader<Reservation> reader = new RepositoryItemReader<>();

        reader.setRepository(repository);
        reader.setMethodName("findByStatus");
        reader.setArguments(List.of(ReservationStatus.PENDING));

        reader.setPageSize(10);
        reader.setSort(Map.of("id", Sort.Direction.ASC));

        return reader;
    }

    @Bean
    public ItemProcessor<Reservation, Reservation> reservationProcessor() {
        return reservation -> {
            if (reservation.getCreatedAt()
                    .isBefore(LocalDateTime.now().minusHours(12))) {

                reservation.setStatus(ReservationStatus.EXPIRED);
                return reservation;
            }
            return null; // skip
        };
    }

    @Bean
    public RepositoryItemWriter<Reservation> reservationWriter(ReservationRepository repository) {

        RepositoryItemWriter<Reservation> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");

        return writer;
    }

    @Bean
    public Step reservationStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 ItemReader<Reservation> reservationReader,
                                 ItemProcessor<Reservation, Reservation> reservationProcessor,
                                 ItemWriter<Reservation> reservationWriter) {

        return new StepBuilder("reservationStep", jobRepository)
                .<Reservation, Reservation>chunk(10, transactionManager)
                .reader(reservationReader)
                .processor(reservationProcessor)
                .writer(reservationWriter)
                .build();
    }

    @Bean
    public Job reservationJob(JobRepository jobRepository, Step reservationStep) {

        return new JobBuilder("reservationJob", jobRepository)
                .start(reservationStep)
                .build();
    }
}
