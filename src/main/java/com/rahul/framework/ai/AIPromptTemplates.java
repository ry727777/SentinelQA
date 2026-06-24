package com.rahul.framework.ai;

/**
 * AI prompt templates for various automation testing scenarios.
 * Provides formatted prompts to instruct AI on expected behavior and response format.
 */
public class AIPromptTemplates {

    /**
     * Build a prompt for AI to analyze test failure root cause.
     * Instructs AI to act as a senior SDET and explain probable root cause
     * in 2-3 sentences. Possible categories: locator issue, assertion mismatch,
     * timing issue, environment issue.
     *
     * @param testName  the name of the failed test
     * @param stackTrace the stack trace or error message from the failure
     * @return formatted prompt string for OpenAI API
     */
    public static String buildFailureAnalysisPrompt(String testName, String stackTrace) {
        // TODO: Build a detailed prompt instructing AI to:
        // - Act as a senior SDET (Software Development Engineer in Test)
        // - Analyze the provided test name and stack trace
        // - Identify probable root cause (locator issue / assertion mismatch / timing issue / environment issue)
        // - Provide explanation in 2-3 sentences
        // - Be concise and actionable

        return null;
    }

    /**
     * Build a prompt for general test recommendation.
     *
     * @param testScenario description of the test scenario
     * @return formatted prompt string
     */
    public static String buildTestRecommendationPrompt(String testScenario) {
        // TODO: Build a prompt for general test recommendations
        return null;
    }
}
