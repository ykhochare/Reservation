package com.example.Reservation.config;

import com.example.Reservation.entities.Cancellation;
import com.example.Reservation.enums.RefundStatus;
import com.example.Reservation.repositories.CancellationRepository;
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
import org.springframework.batch.core.Job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class CancellationBatchConfig {

    @Bean
    public RepositoryItemReader<Cancellation> reader(CancellationRepository repository) {

        RepositoryItemReader<Cancellation> reader = new RepositoryItemReader<>();

        reader.setRepository(repository);
        reader.setMethodName("findByRefundStatus");
        reader.setArguments(List.of(RefundStatus.PENDING));

        reader.setPageSize(10);
        reader.setSort(Map.of("id", Sort.Direction.ASC));

        return reader;
    }

    @Bean
    public ItemProcessor<Cancellation, Cancellation> processor() {
        return cancellation -> {
            if (cancellation.getCancelledAt()
                    .isBefore(LocalDateTime.now().minusHours(48))) {

                cancellation.setRefundStatus(RefundStatus.OVERDUE);
                return cancellation;
            }
            return null; // skip
        };
    }

    @Bean
    public RepositoryItemWriter<Cancellation> writer(CancellationRepository repository) {

        RepositoryItemWriter<Cancellation> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");

        return writer;
    }

    @Bean
    public Step cancellationStep(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     ItemReader<Cancellation> reader,
                     ItemProcessor<Cancellation, Cancellation> processor,
                     ItemWriter<Cancellation> writer) {

        return new StepBuilder("refundStep", jobRepository)
                .<Cancellation, Cancellation>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job cancellationJob(JobRepository jobRepository, Step cancellationStep) {

        return new JobBuilder("refundJob", jobRepository)
                .start(cancellationStep)
                .build();
    }


}
