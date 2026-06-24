package com.rahul.framework.ai;

/**
 * AI prompt templates for various automation testing scenarios.
 * Provides formatted prompts to instruct AI on expected behavior and response format.
 * Used with Gemini API for test failure analysis and recommendations.
 */
public class AIPromptTemplates {

    /**
     * Build a prompt for AI to analyze test failure root cause.
     * Instructs AI to act as a senior SDET and return a structured RCA that can
     * be attached directly to Allure.
     *
     * @param testName  the name of the failed test
     * @param stackTrace the stack trace or error message from the failure
     * @return formatted prompt string for Gemini API
     */
    public static String buildFailureAnalysisPrompt(String testName, String stackTrace) {
        return "You are a senior SDET and test automation architect. Your job is to analyze a failed " +
                "automated test and produce a clear root cause analysis for an Allure report.\n\n" +
                "Rules:\n" +
                "- Use only the evidence from the test name and stack trace.\n" +
                "- Be specific. Mention the likely page object, test class, method, endpoint, locator, or config area when visible.\n" +
                "- Do not give a generic one-line answer.\n" +
                "- Do not say only 'check the stack trace'. Explain what the stack trace means.\n" +
                "- If the failure is caused by test code or configuration, say that clearly.\n" +
                "- Keep the answer practical for a QA automation engineer.\n\n" +
                "Classify the failure into exactly one category:\n" +
                "LOCATOR ISSUE, ASSERTION MISMATCH, TIMING ISSUE, API AUTH/CONFIG ISSUE, ENVIRONMENT ISSUE, APP BUG, TEST BUG, UNKNOWN.\n\n" +
                "Return the analysis in exactly this format:\n\n" +
                "AI ROOT CAUSE ANALYSIS\n" +
                "Category: <one category>\n" +
                "Confidence: <High/Medium/Low>\n\n" +
                "Probable Root Cause:\n" +
                "<2-4 sentences explaining the most likely reason for the failure.>\n\n" +
                "Evidence From Failure:\n" +
                "- <specific exception, assertion, status code, method, or stack trace clue>\n" +
                "- <another concrete clue if available>\n\n" +
                "Most Likely Impacted Area:\n" +
                "- <file/class/method/locator/endpoint/config to inspect first>\n\n" +
                "Recommended Fix:\n" +
                "1. <first practical fix>\n" +
                "2. <second practical fix>\n" +
                "3. <optional verification step>\n\n" +
                "Test Name: " + testName + "\n\n" +
                "Stack Trace / Error Message:\n" +
                stackTrace;
    }

    /**
     * Build a prompt for test recommendations based on a test scenario.
     * Asks AI to suggest test cases and edge cases to cover.
     *
     * @param testScenario description of the test scenario or feature to test
     * @return formatted prompt string for Gemini API
     */
    public static String buildTestRecommendationPrompt(String testScenario) {
        return "You are a senior QA engineer with expertise in test design.\n\n" +
                "A development team is implementing the following feature:\n\n" +
                testScenario + "\n\n" +
                "Provide 5-7 critical test cases (happy path + edge cases) that should be automated. " +
                "For each test case, include:\n" +
                "1. Test name\n" +
                "2. Preconditions\n" +
                "3. Steps\n" +
                "4. Expected result\n\n" +
                "Focus on:\n" +
                "- Happy path (valid input, expected behavior)\n" +
                "- Boundary conditions (empty input, max length, special characters)\n" +
                "- Error scenarios (invalid input, missing required fields)\n" +
                "- State transitions (if applicable)\n\n" +
                "Format as a numbered list for easy reading.";
    }

    /**
     * Build a prompt for API response validation suggestions.
     * Asks AI to suggest validation checks for an API response.
     *
     * @param apiEndpoint the API endpoint being tested
     * @param requestBody the request body sent to the API
     * @param responseBody the response received from the API
     * @return formatted prompt string for Gemini API
     */
    public static String buildApiValidationPrompt(String apiEndpoint, String requestBody, String responseBody) {
        return "You are a senior SDET specializing in API testing.\n\n" +
                "An API endpoint has returned a response. Analyze the response and suggest validation checks.\n\n" +
                "API Endpoint: " + apiEndpoint + "\n\n" +
                "Request Body:\n" +
                requestBody + "\n\n" +
                "Response Body:\n" +
                responseBody + "\n\n" +
                "Provide 5-7 critical validation checks in REST Assured format that should be done:\n" +
                "1. Status code validation\n" +
                "2. Required field validation\n" +
                "3. Data type validation\n" +
                "4. Business logic validation\n" +
                "5. Edge case handling\n\n" +
                "Format as a checklist for a test automation engineer to implement.";
    }

    /**
     * Build a prompt for test flakiness analysis.
     * Asks AI to identify why a test might be flaky and suggest fixes.
     *
     * @param testName the name of the flaky test
     * @param failureHistory description of when/how the test fails
     * @return formatted prompt string for Gemini API
     */
    public static String buildFlakinessDiagnosisPrompt(String testName, String failureHistory) {
        return "You are a senior automation engineer specializing in flaky test diagnosis and remediation.\n\n" +
                "A test is intermittently failing (flaky). Analyze the problem and suggest solutions.\n\n" +
                "Test Name: " + testName + "\n\n" +
                "Failure Pattern:\n" +
                failureHistory + "\n\n" +
                "Common flakiness causes:\n" +
                "1. Hard-coded waits instead of dynamic waits\n" +
                "2. Race conditions (element appears/disappears randomly)\n" +
                "3. Timing-dependent logic\n" +
                "4. Network delays\n" +
                "5. Test data state (database inconsistency)\n" +
                "6. Environment-specific issues\n\n" +
                "Provide:\n" +
                "1. Most likely root cause (2-3 sentences)\n" +
                "2. 3 specific fixes to make the test stable\n" +
                "3. How to verify the test is now stable";
    }

    /**
     * Build a prompt for test strategy recommendation.
     * Asks AI to suggest optimal test strategy for a feature.
     *
     * @param featureDescription description of the feature/module to test
     * @return formatted prompt string for Gemini API
     */
    public static String buildTestStrategyPrompt(String featureDescription) {
        return "You are a senior QA strategist with expertise in test planning and design.\n\n" +
                "A team is building the following feature and needs a test strategy:\n\n" +
                featureDescription + "\n\n" +
                "Provide a testing strategy that includes:\n" +
                "1. Test levels (unit / integration / API / UI) and recommended tools\n" +
                "2. Coverage goals (happy path + edge cases + error scenarios)\n" +
                "3. Number of test cases needed\n" +
                "4. Critical vs non-critical tests\n" +
                "5. Automation vs manual testing split\n" +
                "6. Entry and exit criteria\n\n" +
                "Keep the strategy practical and implementable for a team of 3 SDET engineers " +
                "with 2-week sprint cycles.";
    }
}
