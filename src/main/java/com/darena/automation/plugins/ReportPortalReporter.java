package com.darena.automation.plugins;

import com.darena.automation.util.ReportPortal;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportPortalReporter implements ConcurrentEventListener {

    private String launchUuid;
    private String suiteUuid;
    private String testUuid;
    private String stepUuid;
    private Map<String, String> suites = new HashMap<>();
    private String featureFileName;
    private String scenarioName;
    private ReportPortal reportPortal;

    private static Logger logger = Logger.getLogger(ReportPortalReporter.class.getName());

    public ReportPortalReporter(String reportPortalConfigFile) {
        // this.out = new NiceAppendable(out);
        try {
            reportPortal = new ReportPortal(reportPortalConfigFile);
        } catch (Exception ex) {
            logger.log(Level.SEVERE,"There was an error reading the properties file :"+ex.getMessage());
        }
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestRunStarted.class, this::testRunStarted);
        eventPublisher.registerHandlerFor(TestRunFinished.class, this::testRunFinished);
        eventPublisher.registerHandlerFor(TestCaseStarted.class, this::testCaseStartedEvent);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::testCaseFinishedEvent);
        eventPublisher.registerHandlerFor(TestStepStarted.class, this::stepStarted);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::stepFinished);

    }

    /**
     * This event is triggered when a scenario starts to run
     *
     * @param event
     */
    public void testRunStarted(TestRunStarted event) {
        try {
            JSONObject launch = reportPortal.startLaunch(  event.getInstant().toString(), null);
            launchUuid = launch.get("id").toString();
            logger.log(Level.INFO, "A new launch was created in report portal "+reportPortal.getLaunchUrl(launch.get("number").toString()));
        } catch (Exception ex) {
           logger.log(Level.SEVERE,"An error happened when registering the launch in report portal: "+ex.getMessage());
        }
    }

    /**
     * This event is triggered when a scenario ends the run
     *
     * @param event
     */
    public void testRunFinished(TestRunFinished event) {
        try {

            reportPortal.finishLaunch( launchUuid,event.getInstant().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("------TEST RUN FINISHED: " + event.getInstant().toString());
    }

    /**
     * When a scenario starts to run
     *
     * @param event
     */
    public void testCaseStartedEvent(TestCaseStarted event) {
        try {
            String suiteName = "Feature: " + getFeatureDescription(event.getTestCase().getUri().getPath());
            if (!suites.containsKey(suiteName)) {
                //create suite
                Map<String,String> extraFields = new HashMap<>();
                extraFields.put("codeRef",event.getTestCase().getUri().getPath());
                suiteUuid = reportPortal.startItem( launchUuid,null, suiteName, "suite", event.getInstant().toString(),extraFields,null).get("id").toString();
                suites.put(suiteName, suiteUuid);
            } else {
                //use existing suite
                suiteUuid = suites.get(suiteName);
            }
            //log test item
            Map<String,String> attributes = new HashMap<>();
            for(String tag: event.getTestCase().getTags()){
                attributes.put("testType",tag);
            }
            featureFileName = new File(event.getTestCase().getUri()).getName();
            scenarioName = event.getTestCase().getName();
            testUuid = reportPortal.startItem(launchUuid, suiteUuid, "Scenario: " + event.getTestCase().getName(), "test", event.getInstant().toString(),null,attributes).get("id").toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("------SCENARIO STARTED: " + event.getTestCase().getName() + " " + event.getTestCase().getLine() + " "+event.getTestCase().getId().toString());
    }

    /**
     * When a scenario finishes to run
     *
     * @param event
     */
    public void testCaseFinishedEvent(TestCaseFinished event) {
        try {
            reportPortal.finishItem(launchUuid, testUuid, event.getInstant().toString(), event.getResult().getStatus().name().toLowerCase());
            //finish test suite
            String suiteName = StringUtils.substringAfterLast(event.getTestCase().getUri().toString(), "/");
            if (isLastScenario(event.getTestCase().getName(), event.getTestCase().getUri().getPath())) {
                reportPortal.finishItem(launchUuid, suiteUuid, event.getInstant().toString(), null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("------SCENARIO FINISHED: " + event.getTestCase().getName() + " " + event.getTestCase().getUri().toString() + " " + event.getTestCase().getName());
    }

    /**
     * When a step start to run
     *
     * @param event
     */
    public void stepStarted(TestStepStarted event) {
        try {
            if (event.getTestStep() instanceof PickleStepTestStep) {
                PickleStepTestStep pickleStepTestStep = (PickleStepTestStep) event.getTestStep();
                String stepLine = pickleStepTestStep.getStep().getKeyWord() + pickleStepTestStep.getStep().getText();
                stepUuid = reportPortal.startItem( launchUuid, testUuid, stepLine, "step", event.getInstant().toString(),null,null).get("id").toString();
            } else {
                HookTestStep hookTestStep = (HookTestStep) event.getTestStep();
                stepUuid = reportPortal.startItem( launchUuid, testUuid, event.getTestStep().getCodeLocation(), "step", event.getInstant().toString(),null,null).get("id").toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("------STEP STARTED: " + event.getTestStep().toString());
    }

    /**
     * When a step finished the run
     *
     * @param event
     */
    public void stepFinished(TestStepFinished event) {
        try {
            if (event.getResult().getStatus().is(Status.FAILED)) {
                File screenshotFile = null;
                String screenshot = "target/screenshots/" + featureFileName + "_" + scenarioName + ".png";
                screenshotFile = new File(screenshot);
                System.out.println("SCREEENSHOT " + screenshotFile.getPath() + "");
                reportPortal.addLog( launchUuid, stepUuid, event.getInstant().toString(), screenshotFile, event.getResult().getError().getMessage());
            }
            reportPortal.finishItem( launchUuid, stepUuid, event.getInstant().toString(), event.getResult().getStatus().name().toLowerCase());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("------STEP FINISHED: " + event.getResult().getStatus().name());
    }

    /**
     * This method is a workaround. I couldn't find a way to know when the execution of a feature file ends
     * So this method gives me the last scenario of the current feature file, with that info I know if the current scenario is the last in the feature file
     *
     * @param scenarioDescription
     * @param featureFilePath
     * @return
     */
    public boolean isLastScenario(String scenarioDescription, String featureFilePath) {
        try {
            List<String> featureFile = FileUtils.readLines(new File(featureFilePath), "utf8");
            for(int i = featureFile.size() -1 ; i > -1; i--){
                String line = featureFile.get(i).trim();
                if(line.startsWith("Scenario:") || line.startsWith("Scenario Outline:") ){
                    String lastScenarioDescription = !StringUtils.substringAfter(line,"Scenario Outline:").isEmpty() ? StringUtils.substringAfter(line,"Scenario Outline:"): StringUtils.substringAfter(line,"Scenario:");
                    if(lastScenarioDescription.equalsIgnoreCase(scenarioDescription)){
                        return true;
                    }else {
                        return false;
                    }
                }
            }
        } catch (Exception ex) {

        }
        return false;
    }

    /**
     * This method returns the description of the feature
     *
     * @param featureFilePath
     * @return
     * @throws Exception
     */
    public String getFeatureDescription(String featureFilePath) throws Exception {
        String featureFile = FileUtils.readFileToString(new File(featureFilePath), "utf8");
        String featureDescription = StringUtils.substringBetween(featureFile, "Feature:", "\n");
        return featureDescription;
    }
}
