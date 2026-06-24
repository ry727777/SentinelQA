package com.rahul.framework.utils;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

/**
 * Utility class for capturing screenshots during test execution.
 * Saves screenshots to test-output/screenshots/ directory.
 */
public class ScreenshotUtil {
    private static final Logger logger = LoggerUtil.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "test-output/screenshots/";

    /**
     * Capture a screenshot from the provided WebDriver instance.
     * Saves the screenshot as PNG with the test name as filename.
     *
     * @param driver   the WebDriver instance
     * @param testName the name of the test (used as filename)
     * @return path to the saved screenshot file
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        // TODO: Create SCREENSHOT_DIR if it doesn't exist
        // TODO: Generate filename from testName with timestamp
        // TODO: Use TakesScreenshot to capture screen
        // TODO: Save screenshot as PNG file
        // TODO: Log screenshot path
        // TODO: Return the file path
        // TODO: Handle exceptions
        logger.info("Capturing screenshot for test: " + testName);
        return null;
    }

    /**
     * Take screenshot and return as byte array.
     *
     * @param driver the WebDriver instance
     * @return screenshot as byte array
     */
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        // TODO: Use TakesScreenshot to capture as bytes
        // TODO: Return byte array
        // TODO: Handle exceptions
        return null;
    }
}
