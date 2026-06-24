package com.rahul.framework.ai;

import com.rahul.framework.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

/**
 * Analyzes test failures using AI (Gemini) to determine root cause.
 * Takes test name and stack trace, calls Gemini API to get analysis.
 * Provides fallback message if AI call fails (graceful degradation).
 */
public class FailureAnalyzer {
    private static final Logger logger = LoggerUtil.getLogger(FailureAnalyzer.class);
    private final GeminiClient geminiClient;

    /**
     * Constructor to initialize FailureAnalyzer with Gemini client.
     */
    public FailureAnalyzer() {
        this.geminiClient = new GeminiClient();
        logger.info("FailureAnalyzer initialized with Gemini client");
    }

    /**
     * Analyze a test failure and get AI-powered root cause explanation.
     *
     * @param testName   the name of the failed test
     * @param stackTrace the stack trace/error message from the failure
     * @return AI's root cause analysis as a structured String
     */
    public String analyzeFailure(String testName, String stackTrace) {
        logger.info("Analyzing failure for test: {}", testName);
        logger.debug("Stack trace: {}", stackTrace);

        // Validate inputs
        if (testName == null || testName.isEmpty()) {
            logger.warn("Test name is null or empty, cannot analyze failure");
            return buildLocalAnalysis("Unknown Test", stackTrace, "Test name was missing");
        }

        if (stackTrace == null || stackTrace.isEmpty()) {
            logger.warn("Stack trace is null or empty for test: {}", testName);
            return buildLocalAnalysis(testName, "No stack trace available", "Stack trace was missing");
        }

        try {
            // Check if API key is set before attempting API call
            if (!geminiClient.isApiKeySet()) {
                logger.warn("Gemini API key not set. Returning default analysis without AI.");
                return buildLocalAnalysis(testName, stackTrace, "Gemini API key is not configured");
            }

            // Build prompt using AIPromptTemplates
            String prompt = AIPromptTemplates.buildFailureAnalysisPrompt(testName, stackTrace);
            logger.debug("Built prompt for AI analysis");

            // Call Gemini API to get completion
            logger.info("Calling Gemini API for failure analysis");
            String aiAnalysis = geminiClient.getCompletion(prompt);

            if (aiAnalysis == null || aiAnalysis.isEmpty()) {
                logger.warn("Gemini API returned empty response for test: {}", testName);
                return buildLocalAnalysis(testName, stackTrace, "Gemini API returned empty response");
            }

            logger.info("Successfully received AI analysis for test: {}", testName);
            logger.info("AI Analysis: {}", aiAnalysis);

            return aiAnalysis;

        } catch (Exception e) {
            // Graceful error handling - return fallback message instead of crashing
            logger.error("Failed to get AI analysis for test '{}': {}. Error: {}", 
                    testName, e.getClass().getSimpleName(), e.getMessage());
            logger.debug("Full exception:", e);

            // Return a helpful fallback message
            String fallbackAnalysis = buildFallbackAnalysis(testName, stackTrace, e);
            logger.info("Returning fallback analysis: {}", fallbackAnalysis);
            return fallbackAnalysis;
        }
    }

    /**
     * Build a fallback analysis when AI call fails.
     * Attempts basic pattern matching on stack trace to categorize the failure.
     *
     * @param testName the test name
     * @param stackTrace the error stack trace
     * @param exception the exception that was thrown during AI call
     * @return fallback analysis message
     */
    private String buildFallbackAnalysis(String testName, String stackTrace, Exception exception) {
        String reason = "Gemini analysis failed with " + exception.getClass().getSimpleName() + ": " + exception.getMessage();
        return buildLocalAnalysis(testName, stackTrace, reason);
    }

    /**
     * Build a detailed local RCA when Gemini is unavailable or returns an error.
     */
    private String buildLocalAnalysis(String testName, String stackTrace, String aiStatus) {
        String category = detectCategory(stackTrace);
        String exceptionType = extractExceptionType(stackTrace);
        String exceptionMessage = extractExceptionMessage(stackTrace);
        String impactedArea = extractFirstProjectFrame(stackTrace);
        String confidence = determineConfidence(category, impactedArea);

        StringBuilder analysis = new StringBuilder();
        analysis.append("AI ROOT CAUSE ANALYSIS\n");
        analysis.append("Analysis Mode: Local fallback analysis\n");
        analysis.append("AI Service Status: ").append(aiStatus).append("\n");
        analysis.append("Category: ").append(category).append("\n");
        analysis.append("Confidence: ").append(confidence).append("\n\n");

        analysis.append("Probable Root Cause:\n");
        analysis.append(buildProbableRootCause(category, testName, exceptionType, exceptionMessage, impactedArea)).append("\n\n");

        analysis.append("Evidence From Failure:\n");
        analysis.append("- Test name: ").append(testName).append("\n");
        analysis.append("- Exception type: ").append(exceptionType).append("\n");
        analysis.append("- Exception message: ").append(shorten(exceptionMessage, 350)).append("\n");
        analysis.append("- First project stack frame: ").append(impactedArea).append("\n\n");

        analysis.append("Most Likely Impacted Area:\n");
        analysis.append("- ").append(impactedArea).append("\n\n");

        analysis.append("Recommended Fix:\n");
        appendRecommendedFix(analysis, category);

        analysis.append("\nWhy This Helps:\n");
        analysis.append("This RCA is generated from the exception pattern and the first framework/test stack frame. ");
        analysis.append("Use it as the first investigation path, then confirm with the screenshot and full stack trace attached in Allure.");

        return analysis.toString();
    }

    private String detectCategory(String stackTrace) {
        if (stackTrace == null) {
            return "UNKNOWN";
        }

        if (stackTrace.contains("NoSuchElementException") || stackTrace.contains("Unable to locate element")) {
            return "LOCATOR ISSUE";
        }
        if (stackTrace.contains("TimeoutException") || stackTrace.contains("Wait.until")) {
            return "TIMING ISSUE";
        }
        if (stackTrace.contains("Expected status code <401>") || stackTrace.contains("but was <401>")
                || stackTrace.contains("Unauthorized")) {
            return "API AUTH/CONFIG ISSUE";
        }
        if (stackTrace.contains("AssertionError") || stackTrace.contains("expected") || stackTrace.contains("Expected")) {
            return "ASSERTION MISMATCH";
        }
        if (stackTrace.contains("Connection refused") || stackTrace.contains("UnknownHostException")
                || stackTrace.contains("SocketTimeoutException") || stackTrace.contains("IOException")) {
            return "ENVIRONMENT ISSUE";
        }
        if (stackTrace.contains("NullPointerException") || stackTrace.contains("IllegalStateException")) {
            return "TEST BUG";
        }
        return "UNKNOWN";
    }

    private String extractExceptionType(String stackTrace) {
        if (stackTrace == null || stackTrace.isBlank()) {
            return "Unknown";
        }

        String firstLine = stackTrace.split("\\R", 2)[0].trim();
        int colonIndex = firstLine.indexOf(":");
        return colonIndex > 0 ? firstLine.substring(0, colonIndex).trim() : firstLine;
    }

    private String extractExceptionMessage(String stackTrace) {
        if (stackTrace == null || stackTrace.isBlank()) {
            return "No exception message available";
        }

        String firstLine = stackTrace.split("\\R", 2)[0].trim();
        int colonIndex = firstLine.indexOf(":");
        return colonIndex >= 0 && colonIndex + 1 < firstLine.length()
                ? firstLine.substring(colonIndex + 1).trim()
                : firstLine;
    }

    private String extractFirstProjectFrame(String stackTrace) {
        if (stackTrace == null || stackTrace.isBlank()) {
            return "No project stack frame available";
        }

        String[] lines = stackTrace.split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.contains("com.rahul.")) {
                return trimmed.replaceFirst("^at\\s+", "");
            }
        }

        return "No com.rahul.* stack frame found. Inspect the top stack trace lines.";
    }

    private String determineConfidence(String category, String impactedArea) {
        if (!"UNKNOWN".equals(category) && impactedArea.contains("com.rahul.")) {
            return "High";
        }
        if (!"UNKNOWN".equals(category)) {
            return "Medium";
        }
        return "Low";
    }

    private String buildProbableRootCause(String category, String testName, String exceptionType,
                                          String exceptionMessage, String impactedArea) {
        switch (category) {
            case "LOCATOR ISSUE":
                return "The failure is most likely caused by a changed, incorrect, or stale Selenium locator. "
                        + "Selenium could not find the target element while executing '" + testName + "'. "
                        + "Start by checking the locator used near " + impactedArea + " and compare it with the current application DOM.";
            case "TIMING ISSUE":
                return "The test likely interacted with the page before the required element or state was ready. "
                        + "This usually happens when the page loads slowly, an element appears after an async action, or the test relies on implicit waits only. "
                        + "Add an explicit wait around the action near " + impactedArea + ".";
            case "API AUTH/CONFIG ISSUE":
                return "The API request appears to be rejected before the endpoint logic is processed. "
                        + "A 401 response usually means the API key, token, auth header, or environment configuration is missing or invalid. "
                        + "Check BaseApiTest, config.properties, and environment variables used for API authentication.";
            case "ASSERTION MISMATCH":
                return "The application/API responded, but the actual result did not match the expected condition in the test. "
                        + "This may be a real application bug, changed expected behavior, or outdated assertion logic. "
                        + "Review the assertion and response/page state around " + impactedArea + ".";
            case "ENVIRONMENT ISSUE":
                return "The failure looks related to environment, network, service availability, browser startup, or external dependency access. "
                        + "The test may be correct, but the execution environment could not complete the required operation. "
                        + "Check network access, target URLs, service health, browser/driver setup, and local permissions.";
            case "TEST BUG":
                return "The failure appears to come from the test framework or test code rather than the application behavior. "
                        + "The exception indicates invalid test state, missing initialization, null object usage, or incorrect setup. "
                        + "Inspect the setup and object creation path near " + impactedArea + ".";
            default:
                return "The failure does not match a strong known pattern from the available stack trace. "
                        + "The top exception is " + exceptionType + " with message: " + shorten(exceptionMessage, 220) + ". "
                        + "Use the first project frame and attached stack trace to identify the failing action.";
        }
    }

    private void appendRecommendedFix(StringBuilder analysis, String category) {
        switch (category) {
            case "LOCATOR ISSUE":
                analysis.append("1. Open the impacted page object and verify the locator against the current DOM.\n");
                analysis.append("2. Prefer stable attributes such as id, data-test, or data-testid instead of fragile XPath when possible.\n");
                analysis.append("3. Rerun the failed test and confirm the element is found before continuing the flow.\n");
                break;
            case "TIMING ISSUE":
                analysis.append("1. Add an explicit wait for visibility/clickability before interacting with the element.\n");
                analysis.append("2. Avoid relying only on implicit waits for dynamic UI behavior.\n");
                analysis.append("3. Rerun the test multiple times to confirm the failure is not flaky.\n");
                break;
            case "API AUTH/CONFIG ISSUE":
                analysis.append("1. Verify the API key/token is present and sent in the expected request header.\n");
                analysis.append("2. Check config.properties and environment variables used by BaseApiTest.\n");
                analysis.append("3. Log the request headers safely without exposing secrets and retry the failing endpoint.\n");
                break;
            case "ASSERTION MISMATCH":
                analysis.append("1. Capture/log the actual value or response body before the assertion.\n");
                analysis.append("2. Confirm whether the expected value is still valid for the current application behavior.\n");
                analysis.append("3. Update either the application defect or the test assertion based on the confirmed requirement.\n");
                break;
            case "ENVIRONMENT ISSUE":
                analysis.append("1. Verify the target application/API is reachable from the test machine.\n");
                analysis.append("2. Check browser, driver, network, and local permission setup.\n");
                analysis.append("3. Rerun after confirming the environment dependency is healthy.\n");
                break;
            case "TEST BUG":
                analysis.append("1. Check test setup, object initialization, and shared state before the failing method.\n");
                analysis.append("2. Confirm the driver/request spec/page object is not null before use.\n");
                analysis.append("3. Add guard logs around setup to make future failures easier to diagnose.\n");
                break;
            default:
                analysis.append("1. Start from the first project stack frame listed above.\n");
                analysis.append("2. Compare the failing step with the attached screenshot, logs, and full stack trace.\n");
                analysis.append("3. Add a more specific assertion or log message around the failing action.\n");
                break;
        }
    }

    private String shorten(String value, int maxLength) {
        if (value == null) {
            return "Not available";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    /**
     * Analyze multiple failures (batch analysis).
     * Useful for analyzing multiple test failures from a test run.
     *
     * @param failures array of Failure objects (testName, stackTrace pairs)
     * @return array of analysis strings corresponding to each failure
     */
    public String[] analyzeMultipleFailures(Failure[] failures) {
        logger.info("Analyzing {} test failures", failures.length);
        String[] analyses = new String[failures.length];

        for (int i = 0; i < failures.length; i++) {
            analyses[i] = analyzeFailure(failures[i].testName, failures[i].stackTrace);
            logger.debug("Analyzed failure {}/{}: {}", i + 1, failures.length, analyses[i]);
        }

        logger.info("Completed analysis of all {} failures", failures.length);
        return analyses;
    }

    /**
     * Inner class to represent a test failure for batch analysis.
     */
    public static class Failure {
        public final String testName;
        public final String stackTrace;

        public Failure(String testName, String stackTrace) {
            this.testName = testName;
            this.stackTrace = stackTrace;
        }
    }
}
