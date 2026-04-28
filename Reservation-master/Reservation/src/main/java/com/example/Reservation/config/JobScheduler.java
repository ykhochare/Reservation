package com.example.Reservation.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class JobScheduler {

    private final JobLauncher jobLauncher;

    private final Job cancellationJob;

    private final Job reservationJob;

    private final Job expireWaitingJob;

    public JobScheduler(JobLauncher jobLauncher, Job cancellationJob, Job reservationJob, Job expireWaitingJob) {
        this.jobLauncher = jobLauncher;
        this.cancellationJob = cancellationJob;
        this.reservationJob = reservationJob;
        this.expireWaitingJob = expireWaitingJob;
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void runCancellationJob() throws Exception {
        jobLauncher.run(cancellationJob,
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters()
        );
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void runReservationJob() throws Exception {

        jobLauncher.run(reservationJob,
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters());

    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runExpireWaitingJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLocalDateTime("runTime", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(expireWaitingJob, params);
    }
}
