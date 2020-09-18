package com.darena.automation.plugins;

import com.darena.automation.Const;
import com.darena.automation.TestContext;
import com.darena.automation.util.ReportPortal;
import com.google.inject.Inject;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diego Arena <diego88arena@gmail.com>
 */
public class ReportPortalPlugin implements ConcurrentEventListener {

    private String launchUuid;
    private String suiteUuid;
    private String testUuid;
    private String stepUuid;
    private String launchNumber;
    private Map<String, String> suites = new HashMap<>();
    private String featureFileName;
    private String scenarioName;
    private String lastStepFailedUuid;
    private ReportPortal reportPortal;

    private static Logger logger = Logger.getLogger(ReportPortalPlugin.class.getName());

    public ReportPortalPlugin(String reportPortalConfigFile) {
        // this.out = new NiceAppendable(out);
        try {
            reportPortal = new ReportPortal(reportPortalConfigFile);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "There was an error reading the properties file :" + ex.getMessage());
        }
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        //test run events
        eventPublisher.registerHandlerFor(TestRunStarted.class, this::testRunStarted);
        eventPublisher.registerHandlerFor(TestRunFinished.class, this::testRunFinished);
        //scenario events
        eventPublisher.registerHandlerFor(TestCaseStarted.class, this::scenarioStartedEvent);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::scenarioFinishedEvent);
        //steps events
        eventPublisher.registerHandlerFor(TestStepStarted.class, this::stepStarted);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::stepFinished);
        // other events
        eventPublisher.registerHandlerFor(EmbedEvent.class, this::embedEvent);
    }

    /**
     * This event is triggered when the test run starts to run
     *
     * @param event
     */
    public void testRunStarted(TestRunStarted event) {
        try {
            JSONObject launch = reportPortal.startLaunch(event.getInstant().toString(), null);
            launchUuid = launch.get("id").toString();
            launchNumber = launch.get("number").toString();
            logger.log(Level.INFO, "*** Launch report was started in report portal " + reportPortal.getLaunchUrl(launchNumber));
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error happened when starting the launch in report portal: " + ex.getMessage());
        }
    }

    /**
     * This event is triggered when the test run ends
     *
     * @param event
     */
    public void testRunFinished(TestRunFinished event) {
        try {
            reportPortal.finishLaunch(launchUuid, event.getInstant().toString());
            logger.log(Level.INFO, "Launch was finished in report portal " + reportPortal.getLaunchUrl(launchNumber));
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error happened when finishing the launch in report portal: " + ex.getMessage());
        }
    }

    /**
     * When a scenario starts to run
     *
     * @param event
     */
    public void scenarioStartedEvent(TestCaseStarted event) {
        try {
            String suiteName = "Feature: " + getFeatureDescription(event.getTestCase().getUri().getPath());
            if (!suites.containsKey(suiteName)) {
                //create suite
                Map<String, String> extraFields = new HashMap<>();
                extraFields.put("codeRef", event.getTestCase().getUri().getPath());
                suiteUuid = reportPortal.startItem(launchUuid, null, suiteName, "suite", event.getInstant().toString(), extraFields, null).get("id").toString();
                suites.put(suiteName, suiteUuid);
            } else {
                //use existing suite
                suiteUuid = suites.get(suiteName);
            }
            //log test item
            Map<String, String> attributes = new HashMap<>();
            for (String tag : event.getTestCase().getTags()) {
                attributes.put("testType", tag);
            }
            featureFileName = new File(event.getTestCase().getUri()).getName();
            scenarioName = event.getTestCase().getName();
            testUuid = reportPortal.startItem(launchUuid, suiteUuid, "Scenario: " + event.getTestCase().getName(), "test", event.getInstant().toString(), null, attributes).get("id").toString();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error happened when started the scenario item in report portal: " + ex.getMessage());
        }
    }

    /**
     * When a scenario finishes to run
     *
     * @param event
     */
    public void scenarioFinishedEvent(TestCaseFinished event) {
        try {
            reportPortal.finishItem(launchUuid, testUuid, event.getInstant().toString(), event.getResult().getStatus().name().toLowerCase());
            //finish test suite
            String suiteName = StringUtils.substringAfterLast(event.getTestCase().getUri().toString(), "/");
            if (isLastScenario(event.getTestCase().getName(), event.getTestCase().getUri().getPath())) {
                reportPortal.finishItem(launchUuid, suiteUuid, event.getInstant().toString(), null);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error happened when finished the scenario item in report portal: " + ex.getMessage());
        }
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
                stepUuid = reportPortal.startItem(launchUuid, testUuid, stepLine, "step", event.getInstant().toString(), null, null).get("id").toString();
            } else {
                HookTestStep hookTestStep = (HookTestStep) event.getTestStep();
                stepUuid = reportPortal.startItem(launchUuid, testUuid, event.getTestStep().getCodeLocation(), "step", event.getInstant().toString(), null, null).get("id").toString();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error happened when starting the step item in report portal: " + ex.getMessage());
        }
    }

    /**
     * When a step finished the run
     *
     * @param event
     */
    public void stepFinished(TestStepFinished event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep pickleStepTestStep = (PickleStepTestStep) event.getTestStep();
            String stepLine = pickleStepTestStep.getStep().getKeyWord() + pickleStepTestStep.getStep().getText();
        } else {
            HookTestStep hookTestStep = (HookTestStep) event.getTestStep();
        }
        try {
            if (event.getResult().getStatus().is(Status.FAILED)) {
                String stackTraceLog = formatStepLogResult(event.getResult());
                reportPortal.addLog(launchUuid, stepUuid, event.getInstant().toString(), null, stackTraceLog);
                lastStepFailedUuid = stepUuid;
            }
            reportPortal.finishItem(launchUuid, stepUuid, event.getInstant().toString(), event.getResult().getStatus().name().toLowerCase());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error happened when finishing the step item in report portal: " + ex.getMessage());
        }
    }

    /**
     * This event is triggered when scenario.embed method is called (when taking screenshot from Hook class)
     *
     * @param event
     */
    public void embedEvent(EmbedEvent event) {
        try {
            if (lastStepFailedUuid != null) {
                FileUtils.writeByteArrayToFile(new File("target/screenshot/" + event.getTestCase().getId() + ".png"), event.getData());
                File screenshot = new File("target/screenshot/" + event.getTestCase().getId() + ".png");
                reportPortal.addLog(launchUuid, lastStepFailedUuid, event.getInstant().toString(), screenshot, "screenshot");
                lastStepFailedUuid = null;
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error happened when uploading an screenshot of the failure to report portal: " + ex.getMessage());
        }
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
            for (int i = featureFile.size() - 1; i > -1; i--) {
                String line = featureFile.get(i).trim();
                if (line.startsWith("Scenario:") || line.startsWith("Scenario Outline:")) {
                    String lastScenarioDescription = !StringUtils.substringAfter(line, "Scenario Outline:").isEmpty() ? StringUtils.substringAfter(line, "Scenario Outline:") : StringUtils.substringAfter(line, "Scenario:");
                    if (lastScenarioDescription.trim().equalsIgnoreCase(scenarioDescription.trim())) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "The last scenario couldn't be found: " + ex.getMessage());
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

    /**
     * Return log step formatted
     *
     * @param result
     * @return
     */
    public String formatStepLogResult(Result result) {
        StringBuilder builder = new StringBuilder();
        if (result.getStatus().is(Status.FAILED)) {
            StackTraceElement stackTraceElements[] = result.getError().getStackTrace();
            builder.append(result.getError().toString()).append("\n");
            for (StackTraceElement element : stackTraceElements) {
                builder.append(element.toString()).append("\n");
            }
        }
        return builder.toString();
    }
}
