package com.example.cat.adoption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableBatchProcessing
public class CatAdoptionBatchApplication implements CommandLineRunner {
	private static Logger LOGGER = LoggerFactory
			.getLogger(CatAdoptionBatchApplication.class);
	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job processJob;

	@Autowired
	private ApplicationContext context;


	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(CatAdoptionBatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution execution = jobLauncher.run(processJob, jobParameters);
		LOGGER.info("Exit Status {}", execution.getStatus());
		LOGGER.info("Done");
		SpringApplication.exit(context);

	}
}
