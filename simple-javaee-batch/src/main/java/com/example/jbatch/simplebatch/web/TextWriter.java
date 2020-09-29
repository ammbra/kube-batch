package com.example.jbatch.simplebatch.web;

import org.apache.commons.lang3.StringUtils;

import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


class TextWriter {

    private HttpServletResponse httpServletResponse;

    public TextWriter setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
        return this;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public TextWriter beginResponse(int httpStatusCode) throws IOException {
        getHttpServletResponse().setStatus(httpStatusCode);
        getHttpServletResponse().setContentType("text/plain");
        return this;
    }

    public TextWriter endResponse() throws IOException {
        return this;
    }

    public TextWriter write(String s) throws IOException {
        getHttpServletResponse().getWriter().print(s);
        return this;
    }

    public TextWriter writeln(String line) throws IOException {
        return write(line).writeln();
    }

    public TextWriter writeln() throws IOException {
        getHttpServletResponse().getWriter().println("");
        return this;
    }

    public TextWriter writeJobInstance(JobInstance jobInstance) throws IOException {
        return writeln("JobInstance: " + jobInstanceToString(jobInstance));
    }

    public TextWriter writeJobInstances(Collection<JobInstance> jobInstances) throws IOException {
        for (JobInstance jobInstance : jobInstances) {
            writeJobInstance(jobInstance);
        }
        return this;
    }

    public TextWriter writeJobExecution(JobExecution jobExecution) throws IOException {
        return writeln("JobExecution: " + jobExecutionToString(jobExecution));
    }

    public TextWriter writeJobExecutions(Collection<JobExecution> jobExecutions) throws IOException {
        for (JobExecution jobExecution : jobExecutions) {
            writeJobExecution(jobExecution);
        }
        return this;
    }

    public TextWriter writeProperties(Properties props) throws IOException {
        if (props == null) {
            return this;
        }

        for (Enumeration<?> propNames = props.propertyNames(); propNames.hasMoreElements(); ) {
            String propName = (String) propNames.nextElement();
            writeln(propName + "=" + props.getProperty(propName));
        }

        return this;
    }

    public TextWriter writeJobName(String jobName) throws IOException {
        return writeln("JobName: " + jobName);
    }

    public TextWriter writeJobNames(Set<String> jobNames) throws IOException {
        for (String jobName : jobNames) {
            writeJobName(jobName);
        }
        return this;
    }

    public String jobInstanceToString(JobInstance jobInstance) {
        return new StringBuffer().append("instanceId=").append(jobInstance.getInstanceId()).append(
                ", jobName=").append(jobInstance.getJobName()).toString();
    }


    public String jobExecutionToString(JobExecution jobExecution) {
        return new StringBuffer().append("executionId=").append(jobExecution.getExecutionId()).append(
                ", jobName=").append(jobExecution.getJobName()).append(
                ", batchStatus=").append(jobExecution.getBatchStatus()).append(
                ", createTime=").append(jobExecution.getCreateTime()).append(
                ", startTime=").append(jobExecution.getStartTime()).append(
                ", endTime=").append(jobExecution.getEndTime()).append(
                ", lastUpdatedTime=").append( jobExecution.getLastUpdatedTime()).append(
                ", jobParameters=").append(jobExecution.getJobParameters()).toString();
    }

    public TextWriter writeStepExecution(StepExecution stepExecution) throws IOException {
        return writeln("StepExecution: " + stepExecutionToString(stepExecution));
    }

    public TextWriter printStepExecutions(Collection<StepExecution> stepExecutions) throws IOException {
        for (StepExecution stepExecution : stepExecutions) {
            writeStepExecution(stepExecution);
        }
        return this;
    }


    public String stepExecutionToString(StepExecution stepExecution) {
        return "stepExecutionID=" + stepExecution.getStepExecutionId()
                + ", stepName=" + stepExecution.getStepName()
                + ", exitStatus=" + stepExecution.getExitStatus()
                + ", batchStatus=" + stepExecution.getBatchStatus()
                + ", startTime=" + stepExecution.getStartTime()
                + ", endTime=" + stepExecution.getEndTime()
                + ", metrics=" + metricsToString(stepExecution.getMetrics());
    }


    public String metricsToString(Metric[] metrics) {
        List<String> metricStrings = new ArrayList<String>();
        for (Metric metric : metrics) {
            metricStrings.add(metric.getType() + "=" + metric.getValue());
        }
        return "{" + StringUtils.join(metricStrings, ", ") + "}";
    }

}
