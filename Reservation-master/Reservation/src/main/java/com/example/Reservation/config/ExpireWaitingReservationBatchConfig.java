package com.example.Reservation.config;

import com.example.Reservation.entities.Reservation;
import com.example.Reservation.enums.ReservationStatus;
import com.example.Reservation.repositories.ReservationRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class ExpireWaitingReservationBatchConfig {

    @Bean
    public RepositoryItemReader<Reservation> expireWaitingReader(ReservationRepository repository) {
        RepositoryItemReader<Reservation> reader = new RepositoryItemReader<>();
        reader.setRepository(repository);
        reader.setMethodName("findByStatusAndArrivalDateBefore");
        reader.setArguments(List.of(ReservationStatus.WAITING, LocalDate.now()));
        reader.setPageSize(10);
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public ItemProcessor<Reservation, Reservation> expireWaitingProcessor() {
        return reservation -> {
            reservation.setStatus(ReservationStatus.EXPIRED);
            return reservation;
        };
    }

    @Bean
    public RepositoryItemWriter<Reservation> expireWaitingWriter(ReservationRepository repository) {
        RepositoryItemWriter<Reservation> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step expireWaitingStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  RepositoryItemReader<Reservation> expireWaitingReader,
                                  ItemProcessor<Reservation, Reservation> expireWaitingProcessor,
                                  RepositoryItemWriter<Reservation> expireWaitingWriter) {

        return new StepBuilder("expireWaitingStep", jobRepository)
                .<Reservation, Reservation>chunk(10, transactionManager)
                .reader(expireWaitingReader)
                .processor(expireWaitingProcessor)
                .writer(expireWaitingWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Job expireWaitingJob(JobRepository jobRepository, Step expireWaitingStep) {
        return new JobBuilder("expireWaitingJob", jobRepository)
                .start(expireWaitingStep)
                .build();
    }
}
