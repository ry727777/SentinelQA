package com.rahul.framework.ai;

import com.rahul.framework.utils.LoggerUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * HTTP client for Google Gemini API.
 * Uses java.net.http.HttpClient for API calls (no external SDK).
 * API Key is read from GEMINI_API_KEY environment variable.
 * API Endpoint: https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent
 * Model: gemini-2.5-flash (free tier, fast, sufficient for test failure analysis)
 */
public class GeminiClient {
    private static final Logger logger = LoggerUtil.getLogger(GeminiClient.class);
    private static final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private static final String API_KEY_ENV_VAR = "GEMINI_API_KEY";

    private final HttpClient httpClient;
    private final String apiKey;

    /**
     * Constructor to initialize Gemini client with API key from environment.
     */
    public GeminiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = System.getenv(API_KEY_ENV_VAR);

        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Gemini API key not set. Environment variable '{}' is missing. " +
                    "Set it using: export GEMINI_API_KEY=your_key_here", API_KEY_ENV_VAR);
        } else {
            logger.info("Gemini client initialized successfully");
        }
    }

    /**
     * Get AI completion for a given prompt using Google Gemini API.
     *
     * @param prompt the prompt string to send to Gemini
     * @return the AI's response/completion as a String
     * @throws Exception if API call fails or response parsing fails
     */
    public String getCompletion(String prompt) throws Exception {
        if (!isApiKeySet()) {
            throw new RuntimeException("Gemini API key is not set. Cannot proceed with API call.");
        }

        logger.info("Sending prompt to Gemini API");
        logger.debug("Prompt: {}", prompt);

        // Build request JSON payload in Gemini format
        String requestBody = buildGeminiRequestPayload(prompt);
        logger.debug("Request payload: {}", requestBody);

        // Create HTTP POST request to Gemini API endpoint
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        logger.info("Sending request to Gemini API endpoint: {}", API_ENDPOINT);

        // Send request and get response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        logger.info("Received response from Gemini API with status code: {}", response.statusCode());
        logger.debug("Response body: {}", response.body());

        // Check for errors
        if (response.statusCode() != 200) {
            logger.error("Gemini API returned error status: {}. Response: {}", response.statusCode(), response.body());
            throw new RuntimeException("Gemini API call failed with status code: " + response.statusCode() +
                    ". Response: " + response.body());
        }

        // Parse JSON response and extract completion text
        String completion = parseGeminiResponse(response.body());
        logger.info("Successfully received completion from Gemini API");
        logger.debug("Completion: {}", completion);

        return completion;
    }

    /**
     * Build request payload in Gemini API format.
     *
     * @param prompt the user's prompt text
     * @return JSON string formatted for Gemini API
     */
    private String buildGeminiRequestPayload(String prompt) {
        return "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        {\n" +
                "          \"text\": \"" + escapeJsonString(prompt) + "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    /**
     * Parse Gemini API response and extract the generated text.
     * Gemini response format:
     * {
     *   "candidates": [
     *     {
     *       "content": {
     *         "parts": [
     *           {
     *             "text": "response text here"
     *           }
     *         ]
     *       }
     *     }
     *   ]
     * }
     *
     * @param responseBody the raw JSON response from Gemini API
     * @return the extracted text completion
     * @throws Exception if parsing fails or response is malformed
     */
    private String parseGeminiResponse(String responseBody) throws Exception {
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray candidates = root.getAsJsonArray("candidates");

            if (candidates == null || candidates.isEmpty()) {
                logger.error("Malformed Gemini response - candidates not found. Response: {}", responseBody);
                throw new Exception("Malformed Gemini API response: candidates not found");
            }

            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject content = firstCandidate.getAsJsonObject("content");
            JsonArray parts = content != null ? content.getAsJsonArray("parts") : null;

            if (parts == null || parts.isEmpty()) {
                logger.error("Malformed Gemini response - content parts not found. Response: {}", responseBody);
                throw new Exception("Malformed Gemini API response: content parts not found");
            }

            String completion = parts.get(0).getAsJsonObject().get("text").getAsString();

            logger.debug("Extracted text from response: {}", completion);
            return completion;

        } catch (Exception e) {
            logger.error("Failed to parse Gemini API response: {}. Error: {}", responseBody, e.getMessage());
            throw new Exception("Failed to parse Gemini API response: " + e.getMessage(), e);
        }
    }

    /**
     * Escape special characters in JSON string.
     * Escapes: quotes, newlines, carriage returns, tabs, backslashes
     *
     * @param input the raw string to escape
     * @return escaped string safe for JSON
     */
    private String escapeJsonString(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Validate that API key is properly set.
     *
     * @return true if API key is available, false otherwise
     */
    public boolean isApiKeySet() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
