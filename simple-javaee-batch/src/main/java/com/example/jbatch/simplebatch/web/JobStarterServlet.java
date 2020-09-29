package com.example.jbatch.simplebatch.web;

import org.apache.commons.lang3.StringUtils;

import java.util.logging.Logger;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Properties;
import java.io.IOException;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet uses the BatchRuntime JobOperator to start, stop, restart,
 * and get status of jobs.
 */
@WebServlet(urlPatterns = { "/jobstarter" })
public class JobStarterServlet extends HttpServlet {

    protected final static Logger LOGGER = Logger.getLogger(JobStarterServlet.class.getName());

    private TextWriter responseWriter ;

    private JobOperator jobOperator;
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("JobStarterServlet: doGet: URL " + request.getRequestURL() + "?" + request.getQueryString());

        setResponseWriter(request.getHeader("Accept"));

        String action = request.getParameter("action");

        switch (action.toLowerCase()) {
            case "start":
                start(request,response);
                break;
            case "restart":
                restart(request,response);
                break;
            case "status":
                status(request,response);
                break;
            case "stop":
                stop(request, response);
                break;
            case "abandon":
                abort(request, response);
                break;
            case "getJobExecutions":
                getJobExecutions(request,response);
                break;
            case "getJobInstance":
                getJobInstance(request,response);
                break;
            case "getJobInstanceCount":
                getJobInstanceCount(request, response);
                break;
            case "getJobInstances":
                getJobInstances(request, response);
                break;
            case "getJobNames":
                getJobNames(request,response);
                break;
            case "getParameters":
                getParameters(request, response);
                break;
            case "getRunningExecutions":
                getRunningExecutions(request, response);
                break;
            case "getStepExecutions":
                getStepExecutions(request,response);
                break;
            default:
                help(request, response);
                break;
        }
    }

    /**
     * @return the response writer.
     */
    protected TextWriter setResponseWriter(String acceptHeader) {
        if (StringUtils.isEmpty(acceptHeader)) {
            return (responseWriter = new TextWriter());
        } else if ( acceptHeader.contains("text/html") ) {
            return (responseWriter = new HtmlWriter());
        } else {
            return (responseWriter = new TextWriter());
        }
    }

    protected TextWriter getResponseWriter() {
        return (responseWriter != null) ? responseWriter : (responseWriter = new TextWriter());
    }

    protected JobOperator getJobOperator() {
        return (jobOperator != null) ? jobOperator : (jobOperator = BatchRuntime.getJobOperator());
    }

    protected void help(HttpServletRequest request, HttpServletResponse response) throws IOException {

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "help:" )
                           .writeln( "jobstarter?action=help" )
                           .writeln( "jobstarter?action=start&jobXMLName={jobXMLName}&jobParameters={name=value}&jobParameters={name=value}..." )
                           .writeln( "jobstarter?action=restart&executionId={execId}&restartParameters={name=value}&restartParameters={name=value}..." )
                           .writeln( "jobstarter?action=stop&executionId={execId}" )
                           .writeln( "jobstarter?action=abandon&executionId={execId}" )
                           .writeln( "jobstarter?action=status&executionId={execId}" )
                           .writeln( "jobstarter?action=getJobExecution&executionId={execId}" )
                           .writeln( "jobstarter?action=getJobExecutions&instanceId={instanceId}" )
                           .writeln( "jobstarter?action=getJobInstance&executionId={execId}" )
                           .writeln( "jobstarter?action=getJobInstanceCount&jobName={jobName}" )
                           .writeln( "jobstarter?action=getJobInstances&jobName={jobName}&start={start}&count={count}" )
                           .writeln( "jobstarter?action=getJobNames" )
                           .writeln( "jobstarter?action=getParameters&executionId={execId}" )
                           .writeln( "jobstarter?action=getRunningExecutions&jobName={jobName}" )
                           .writeln( "jobstarter?action=getStepExecutions&executionId={execId}" )
                           .endResponse();
    }

    protected void start(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String jobXMLName = getRequiredParm(request, "jobXMLName");
        Properties jobParameters = getJobParameters(request, "jobParameters");

        JobOperator jobOperator = getJobOperator();
        long execId = jobOperator.start(jobXMLName, jobParameters);

        JobInstance jobInstance = jobOperator.getJobInstance(execId);
        JobExecution jobExecution = jobOperator.getJobExecution(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "start(jobXMLName=" + jobXMLName + ", jobParameters=" + jobParameters + "): Job started!" )
                           .writeJobInstance( jobInstance )
                           .writeJobExecution( jobExecution )
                           .endResponse();
    }



    protected void status(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");

        JobOperator jobOperator = getJobOperator();
        JobInstance jobInstance = jobOperator.getJobInstance(execId);
        List<JobExecution> jobExecutions = jobOperator.getJobExecutions(jobInstance);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "status(executionId=" + execId + "): ")
                           .writeJobInstance( jobInstance )
                           .writeJobExecutions( jobExecutions )
                           .endResponse();
    }


    protected void stop(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");

        getJobOperator().stop(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "stop(executionId=" + execId + "): Stop request submitted!");

        status(request, response);
    }


    protected void abort(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");
        getJobOperator().abandon(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "abandon(executionId=" + execId + "): Abandon request submitted!");

        status(request, response);
    }


    protected void restart(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");
        Properties restartParameters =  getJobParameters(request, "restartParameters");

        long newExecId = getJobOperator().restart(execId,restartParameters);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "restart(executionId=" + execId + ", restartParameters=" + restartParameters + "): Job restarted!");

        status(request, response);
    }

    protected void getJobExecution(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");
        JobExecution jobExecution = getJobOperator().getJobExecution(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln("getJobExecution(executionId=" + execId + "): ")
                           .writeJobExecution( jobExecution )
                           .endResponse();
    }


    protected void getJobExecutions(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final long instanceId = getLongParm(request, "instanceId") ;

        List<JobExecution> jobExecutions = getJobOperator().getJobExecutions(getJobOperator().getJobInstance(instanceId));
        
        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "getJobExecutions(instanceId=" + instanceId + "): ")
                           .writeJobExecutions(jobExecutions)
                           .endResponse();
    }


    protected void getJobInstanceCount(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String jobName = getRequiredParm(request, "jobName") ;

        int count = getJobOperator().getJobInstanceCount( jobName );

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "getJobInstanceCount(jobName=" + jobName+ "): " + count)
                           .endResponse();
    }


    protected void getJobInstance(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long execId = getLongParm(request, "executionId");

        JobInstance jobInstance = getJobOperator().getJobInstance(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "getJobInstance(executionId=" + execId + "): " )
                           .writeJobInstance( jobInstance )
                           .endResponse();
    }

    protected void getJobInstances(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String jobName = getRequiredParm(request, "jobName") ;
        int start = (int) getLongParm(request, "start");
        int count = (int) getLongParm(request, "count");

        List<JobInstance> jobInstances = getJobOperator().getJobInstances( jobName, start, count );

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "getJobInstances(jobName=" + jobName + ", start=" + start + ", count=" + count + "): " )
                           .writeJobInstances( jobInstances )
                           .endResponse();
    }


    protected void getJobNames(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Set<String> jobNames = getJobOperator().getJobNames();

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "getJobNames(): ")
                           .writeJobNames( jobNames )
                           .endResponse();
    }


    protected void getParameters(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long execId = getLongParm(request, "executionId");

        Properties props = getJobOperator().getParameters(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "getParameters(executionId=" + execId + "): ")
                           .writeProperties( props )
                           .endResponse();
    }


    protected void getRunningExecutions(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String jobName = getRequiredParm(request, "jobName") ;
        List<Long> execIds = getJobOperator().getRunningExecutions(jobName);

        List<JobExecution> jobExecutions = new ArrayList<JobExecution>();

        for (Long execId : execIds) {
            jobExecutions.add( getJobOperator().getJobExecution( execId ) );
        }

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "getRunningExecutions(jobName=" + jobName + "): ")
                           .writeJobExecutions(jobExecutions)
                           .endResponse();
    }


    protected void getStepExecutions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long execId = getLongParm(request, "executionId");

        List<StepExecution> stepExecutions = getJobOperator().getStepExecutions(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .writeln( "getStepExecutions(executionId=" + execId + "): ")
                           .printStepExecutions(stepExecutions);
    }


    protected long getLongParm(HttpServletRequest request, String parmName) throws IOException {
        return Long.parseLong( getRequiredParm(request, parmName) );
    }

    protected String getRequiredParm(HttpServletRequest request, String queryParmName) throws IOException {
        String queryParmValue = request.getParameter(queryParmName);
        if ( StringUtils.isEmpty(queryParmValue) ) {
            throw new IllegalArgumentException("ERROR: " + queryParmName + " is a required parameter" );
        }
        return queryParmValue;
    }


    protected Properties getJobParameters(HttpServletRequest request, String queryParmName) throws IOException {
        String[] jobParameters = request.getParameterValues(queryParmName);
        if (jobParameters == null ) {
            return null;
        }

        Properties properties = new Properties();

        for (String jobParameter : jobParameters) {
            LOGGER.info("JobStarterServlet: getJobParameters : jobParameter" + jobParameter);

            String[] keyValue = jobParameter.split("=");
            properties.setProperty(keyValue[0], (keyValue.length >= 2) ? keyValue[1] : null);
        }

        LOGGER.info("JobStarterServlet: getJobParameters : retMe: " + properties);

        return properties;
    }
}





