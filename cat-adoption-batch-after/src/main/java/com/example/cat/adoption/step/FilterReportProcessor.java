package com.example.cat.adoption.step;

import com.example.cat.adoption.model.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class FilterReportProcessor implements ItemProcessor<Report, Report> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterReportProcessor.class);

	@Override
	public Report process(Report item) throws Exception {
		if(item.getAge()==1){
			return null;
		}
		return item;
	}

}