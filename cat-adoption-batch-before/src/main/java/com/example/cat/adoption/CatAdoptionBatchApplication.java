package com.example.cat.adoption;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CatAdoptionBatchApplication {
	private static final Logger LOGGER = Logger.getLogger(CatAdoptionBatchApplication.class);


	public static final String JOB_LAUNCHER = "jobLauncher";
	public static final String REPORT_JOB = "reportJob";

	public static void main(String[] args) {

		String[] springConfig  = 
			{
				"spring/batch/config/context.xml",
				"spring/batch/jobs/job-report.xml"
			};
		
		ApplicationContext context = 
				new ClassPathXmlApplicationContext(springConfig);
		
		JobLauncher jobLauncher = (JobLauncher) context.getBean(JOB_LAUNCHER);
		Job job = (Job) context.getBean(REPORT_JOB);

		try {
			LOGGER.info("Batch job has been invoked");
			JobExecution execution = jobLauncher.run(job, new JobParameters());
			LOGGER.info("Exit Status {}", execution.getStatus());

		} catch (Exception e) {
			LOGGER.error(e.toString());
		}

		LOGGER.info("Done");

	}
}
