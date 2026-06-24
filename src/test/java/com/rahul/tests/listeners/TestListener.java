package com.rahul.tests.listeners;

import com.rahul.framework.ai.FailureAnalyzer;
import com.rahul.framework.utils.LoggerUtil;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;

/**
 * TestNG listener for test execution events.
 * Handles test start, success, and failure events.
 * On failure, calls FailureAnalyzer to get AI-powered root cause analysis
 * and attaches it to Allure report.
 */
public class TestListener implements ITestListener {
    private static final Logger logger = LoggerUtil.getLogger(TestListener.class);
    private final FailureAnalyzer failureAnalyzer;

    /**
     * Constructor to initialize TestListener.
     */
    public TestListener() {
        // TODO: Initialize FailureAnalyzer
        this.failureAnalyzer = null;
    }

    /**
     * Called when test execution starts.
     *
     * @param result the test result object
     */
    @Override
    public void onTestStart(ITestResult result) {
        // TODO: Log test start
        // TODO: Include test name and class name
        logger.info("Test started: " + result.getName());
    }

    /**
     * Called when test passes successfully.
     *
     * @param result the test result object
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        // TODO: Log test success
        // TODO: Include test name
        logger.info("Test passed: " + result.getName());
    }

    /**
     * Called when test fails.
     * Analyzes failure using AI and attaches analysis to Allure report.
     *
     * @param result the test result object
     */
    @Override
    public void onTestFailure(ITestResult result) {
        // TODO: Log test failure
        // TODO: Extract test name
        // TODO: Extract stack trace from result
        // TODO: Call failureAnalyzer.analyzeFailure(testName, stackTrace)
        // TODO: Attach AI response to Allure report using Allure.addAttachment()
        // TODO: Log analysis result
        logger.error("Test failed: " + result.getName());
    }

    /**
     * Called when test is skipped.
     *
     * @param result the test result object
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        // TODO: Log test skip
        logger.warn("Test skipped: " + result.getName());
    }

    /**
     * Called when test fails but is within success percentage.
     *
     * @param result the test result object
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.info("Test failed but within success percentage: " + result.getName());
    }

    /**
     * Called when all tests in a suite finish.
     */
    @Override
    public void onFinish(ITestContext context) {
        // TODO: Log suite completion
        logger.info("Test suite finished: " + context.getName());
    }

    /**
     * Called before any tests start in the suite.
     */
    @Override
    public void onStart(ITestContext context) {
        // TODO: Log suite start
        logger.info("Test suite started: " + context.getName());
    }
}
