package com.example.cat.adoption.config;

import com.example.cat.adoption.step.FilterReportProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;

import com.example.cat.adoption.listener.JobCompletionListener;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.core.io.Resource;


@Configuration
public class BatchConfig {

	@Value("${input.xml}")
	Resource xmlReport;

	@Value("${output.csv}")
	Resource csvReport;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;


	@Bean
	public Job processJob() {

		return jobBuilderFactory.get("reportJob")
				.incrementer(new RunIdIncrementer()).listener(listener())
				.flow(runFirstStep()).end().build();
	}

	@Bean
	public Step runFirstStep() {
		Jaxb2Marshaller reportUnmarshaller = new Jaxb2Marshaller();
		reportUnmarshaller.setClassesToBeBound(com.example.cat.adoption.model.Report.class);
		StaxEventItemReader xmlItemReader = new StaxEventItemReader();
		xmlItemReader.setFragmentRootElementName("record");
		xmlItemReader.setResource(xmlReport);
		xmlItemReader.setStrict(false);
		xmlItemReader.setUnmarshaller(reportUnmarshaller);

		FlatFileItemWriter csvItemwriter = new FlatFileItemWriter();
		csvItemwriter.setResource(csvReport);
		csvItemwriter.setForceSync(false);
		csvItemwriter.setAppendAllowed(true);

		DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
		lineAggregator.setDelimiter(",");
		BeanWrapperFieldExtractor fieldExtractor = new BeanWrapperFieldExtractor();
		String[] names = {"refId", "name", "age", "csvDob", "fivPositive"};
		fieldExtractor.setNames(names);
		lineAggregator.setFieldExtractor(fieldExtractor);
		csvItemwriter.setLineAggregator(lineAggregator);


		return stepBuilderFactory.get("step1").<String, String> chunk(5)
				.reader(xmlItemReader).processor(new FilterReportProcessor())
				.writer(csvItemwriter).build();
	}

	@Bean
	public JobExecutionListener listener() {
		return new JobCompletionListener();
	}

}
