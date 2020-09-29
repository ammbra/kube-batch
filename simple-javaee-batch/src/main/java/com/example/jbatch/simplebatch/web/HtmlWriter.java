package com.example.jbatch.simplebatch.web;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import java.io.IOException;


class HtmlWriter extends TextWriter {

    public TextWriter beginResponse(int httpStatusCode) throws IOException {
        getHttpServletResponse().setStatus(httpStatusCode);
        getHttpServletResponse().setContentType("text/html");

        return write("<html>").write("<body>");
    }

    public TextWriter endResponse() throws IOException {
        return write("</body>").write("</html>");
    }

    public TextWriter writeln() throws IOException {
        return write("<br>");
    }


    public TextWriter writeJobName(String jobName) throws IOException {
        write("JobName: " + jobName);
        write(",<br /><a href=" + wrap("jobstarter?action=getJobInstances&jobName=" + jobName + "&start=0&count=100") + ">"
                + "getJobInstances(jobName=" + jobName + ", start=0, count=100)"
                + "</a>");
        write(", <br /><a href=" + wrap("jobstarter?action=getJobInstanceCount&jobName=" + jobName) + ">"
                + "getJobInstanceCount(jobName=" + jobName + ")"
                + "</a>");
        write(", <br /><a href=" + wrap("jobstarter?action=getRunningExecutions&jobName=" + jobName) + ">"
                + "getRunningExecutions(jobName=" + jobName + ")"
                + "</a>");
        return writeln("");
    }


    public TextWriter writeJobInstance(JobInstance jobInstance) throws IOException {
        write("JobInstance: " + jobInstanceToString(jobInstance));
        write(", <br /><a href=" + wrap("jobstarter?action=getJobExecutions&instanceId=" + jobInstance.getInstanceId()) + ">"
                + "getJobExecutions(instanceId=" + jobInstance.getInstanceId() + ")"
                + "</a>");
        return writeln("");
    }

    private String wrap(String s) {
        return "\"" + s + "\"";
    }

    /**
     * Print JobExecution and associated links
     */
    public TextWriter writeJobExecution(JobExecution jobExecution) throws IOException {
        write("JobExecution: " + jobExecutionToString(jobExecution));
        write(", <br /><a href=" + wrap("jobstarter?action=getJobInstance&executionId=" + jobExecution.getExecutionId()) + ">"
                + "getJobInstance(executionId=" + jobExecution.getExecutionId() + ")"
                + "</a>");
        write(", <br /><a href=" + wrap("jobstarter?action=getParameters&executionId=" + jobExecution.getExecutionId()) + ">"
                + "getParameters(executionId=" + jobExecution.getExecutionId() + ")"
                + "</a>");
        write(", <br /><a href=" + wrap("jobstarter?action=getStepExecutions&executionId=" + jobExecution.getExecutionId()) + ">"
                + "getStepExecutions(executionId=" + jobExecution.getExecutionId() + ")"
                + "</a>");

        if (isRunning(jobExecution.getBatchStatus())) {
            write(", <br /><a href=" + wrap("jobstarter?action=stop&executionId=" + jobExecution.getExecutionId()) + ">"
                    + "stop(executionId=" + jobExecution.getExecutionId() + ")"
                    + "</a>");
        }

        return writeln("");
    }

    public boolean isRunning(BatchStatus batchStatus) {
        boolean isRunning = false;
        switch (batchStatus) {
            case STARTED:
            case STARTING:
                isRunning = true;
                break;
            default:
                return isRunning;
        }
        return isRunning;
    }

}
