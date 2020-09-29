package com.example.cat.adoption;

import com.example.cat.adoption.config.BatchConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {CatAdoptionBatchApplication.class, BatchConfig.class})
public class CatAdoptionBatchApplicationTest {


	@Value("${output.csv}")
	Resource csvReport;

	@Test
	public void launchJob() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(csvReport.getFile()));
		int lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		assertTrue(lines>0);
	}


}
