package com.rahul.tests.listeners;

import com.rahul.framework.ai.FailureAnalyzer;
import com.rahul.framework.utils.LoggerUtil;
import com.rahul.framework.utils.ScreenshotUtil;
import com.rahul.framework.config.DriverManager;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
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
        this.failureAnalyzer = new FailureAnalyzer();
        logger.info("TestListener initialized with FailureAnalyzer");
    }

    /**
     * Called when test execution starts.
     */
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getName();
        String className = result.getTestClass().getName();

        logger.info("═══════════════════════════════════════════════════════");
        logger.info("🧪 TEST START: {}", testName);
        logger.info("📍 Class: {}", className);
        logger.info("═══════════════════════════════════════════════════════");
    }

    /**
     * Called when test passes successfully.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getName();
        long duration = result.getEndMillis() - result.getStartMillis();

        logger.info("═══════════════════════════════════════════════════════");
        logger.info("✅ TEST PASSED: {}", testName);
        logger.info("⏱️  Duration: {} ms", duration);
        logger.info("═══════════════════════════════════════════════════════");
    }

    /**
     * Called when test fails.
     * Analyzes failure using AI and attaches analysis to Allure report.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getName();
        String className = result.getTestClass().getName();
        long duration = result.getEndMillis() - result.getStartMillis();

        logger.error("═══════════════════════════════════════════════════════");
        logger.error("❌ TEST FAILED: {}", testName);
        logger.error("📍 Class: {}", className);
        logger.error("⏱️  Duration: {} ms", duration);
        logger.error("═══════════════════════════════════════════════════════");

        // Extract exception/stack trace from failure
        Throwable throwable = result.getThrowable();
        String stackTrace;

        if (throwable != null) {
            stackTrace = getStackTraceAsString(throwable);
            logger.error("🔴 Exception Type: {}", throwable.getClass().getSimpleName());
            logger.error("🔴 Exception Message: {}", throwable.getMessage());
            logger.error("🔴 Stack Trace:\n{}", stackTrace);
        } else {
            stackTrace = "No stack trace available";
            logger.error("⚠️  No exception found in result");
        }

        // Capture screenshot for UI tests
        try {
            if (DriverManager.isDriverSet()) {
                WebDriver driver = DriverManager.getDriver();
                if (driver != null) {
                    String screenshotPath = ScreenshotUtil.captureScreenshot(driver, testName);
                    logger.info("📸 Screenshot captured: {}", screenshotPath);

                    // Attach screenshot to Allure
                    attachScreenshotToAllure(driver, testName);
                }
            }
        } catch (Exception e) {
            logger.warn("⚠️  Could not capture screenshot: {}", e.getMessage());
        }

        // Call AI FailureAnalyzer to get root cause analysis
        logger.info("🤖 Calling AI FailureAnalyzer for root cause analysis...");
        String aiAnalysis;
        try {
            aiAnalysis = failureAnalyzer.analyzeFailure(testName, stackTrace);
            logger.info("✨ AI Analysis received successfully");
            logger.info("🔍 AI Insight: {}", aiAnalysis);
        } catch (Exception e) {
            logger.error("⚠️  FailureAnalyzer encountered an error: {}", e.getMessage());
            aiAnalysis = "AI analysis failed: " + e.getMessage();
        }

        // Attach AI analysis to Allure report
        attachTextToAllure("🤖 AI Root Cause Analysis", aiAnalysis);

        // Attach stack trace to Allure report
        attachTextToAllure("Stack Trace", stackTrace);

        logger.error("═══════════════════════════════════════════════════════");
    }

    /**
     * Called when test is skipped.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getName();

        logger.warn("═══════════════════════════════════════════════════════");
        logger.warn("⏭️  TEST SKIPPED: {}", testName);
        logger.warn("═══════════════════════════════════════════════════════");
    }

    /**
     * Called when test fails but is within success percentage.
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getName();
        logger.info("⚠️  TEST FAILED BUT WITHIN SUCCESS PERCENTAGE: {}", testName);
    }

    /**
     * Called before any tests start in the suite.
     */
    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getName();
        int totalTests = context.getAllTestMethods().length;

        logger.info("╔═══════════════════════════════════════════════════════╗");
        logger.info("║ 🚀 TEST SUITE STARTED");
        logger.info("║ Suite Name: {}", suiteName);
        logger.info("║ Total Tests: {}", totalTests);
        logger.info("╚═══════════════════════════════════════════════════════╝");
    }

    /**
     * Called when all tests in a suite finish.
     */
    @Override
    public void onFinish(ITestContext context) {
        String suiteName = context.getName();
        int totalTests = context.getAllTestMethods().length;
        int passedTests = context.getPassedTests().size();
        int failedTests = context.getFailedTests().size();
        int skippedTests = context.getSkippedTests().size();
        long duration = context.getEndDate().getTime() - context.getStartDate().getTime();

        logger.info("╔═══════════════════════════════════════════════════════╗");
        logger.info("║ ✅ TEST SUITE FINISHED");
        logger.info("║ Suite Name: {}", suiteName);
        logger.info("║ Total Tests: {}", totalTests);
        logger.info("║ ✅ Passed: {}", passedTests);
        logger.info("║ ❌ Failed: {}", failedTests);
        logger.info("║ ⏭️  Skipped: {}", skippedTests);
        logger.info("║ ⏱️  Duration: {} ms", duration);
        double successRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;
        logger.info("║ 📊 Success Rate: {:.2f}%", successRate);
        logger.info("╚═══════════════════════════════════════════════════════╝");
    }

    /**
     * Attach text content to Allure report.
     * Fixed method that works with current Allure version.
     *
     * @param attachmentName the name of the attachment
     * @param content        the text content to attach
     */
    private void attachTextToAllure(String attachmentName, String content) {
        try {
            Allure.addAttachment(attachmentName, "text/plain", content);
            logger.info("✅ '{}' attached to Allure report", attachmentName);
        } catch (Exception e) {
            logger.error("❌ Failed to attach '{}' to Allure: {}", attachmentName, e.getMessage());
        }
    }

    /**
     * Attach screenshot to Allure report.
     * Captures the screenshot and attaches it as an image.
     *
     * @param driver   the WebDriver instance
     * @param testName the test name (for screenshot naming)
     */
    private void attachScreenshotToAllure(WebDriver driver, String testName) {
        byte[] screenshotBytes = ScreenshotUtil.captureScreenshotAsBytes(driver);
        if (screenshotBytes != null && screenshotBytes.length > 0) {
            try {
                Allure.addAttachment(
                        "Failure Screenshot - " + testName,
                        "image/png",
                        new java.io.ByteArrayInputStream(screenshotBytes),
                        ".png");
                logger.info("✅ Screenshot attached to Allure report");
            } catch (Exception e) {
                logger.error("❌ Failed to attach screenshot to Allure: {}", e.getMessage());
            }
        }
    }

    /**
     * Convert exception stack trace to a formatted String.
     */
    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");

        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }

        // Include cause if present
        if (throwable.getCause() != null) {
            sb.append("Caused by: ").append(getStackTraceAsString(throwable.getCause()));
        }

        return sb.toString();
    }
}