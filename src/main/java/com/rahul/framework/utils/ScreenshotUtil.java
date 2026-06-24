package com.rahul.framework.utils;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for capturing screenshots during test execution.
 * Saves screenshots to test-output/screenshots/ directory.
 * Screenshots are captured on test failure and attached to Allure reports.
 */
public class ScreenshotUtil {
    private static final Logger logger = LoggerUtil.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "test-output/screenshots/";

    /**
     * Capture a screenshot from the provided WebDriver instance.
     * Saves the screenshot as PNG with the test name and timestamp as filename.
     *
     * @param driver   the WebDriver instance
     * @param testName the name of the test (used as filename)
     * @return path to the saved screenshot file, or null if capture fails
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            logger.warn("WebDriver is null, cannot capture screenshot");
            return null;
        }

        try {
            // Create screenshot directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                boolean dirCreated = screenshotDir.mkdirs();
                if (dirCreated) {
                    logger.info("Created screenshot directory: {}", SCREENSHOT_DIR);
                } else {
                    logger.warn("Failed to create screenshot directory: {}", SCREENSHOT_DIR);
                }
            }

            // Generate filename from testName with timestamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_DIR + filename;

            // Use TakesScreenshot to capture screen
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File srcFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            // Save screenshot as PNG file
            File destFile = new File(filePath);
            Files.copy(srcFile.toPath(), destFile.toPath());

            logger.info("✅ Screenshot captured successfully");
            logger.info("📸 Screenshot saved at: {}", filePath);

            return filePath;

        } catch (IOException e) {
            logger.error("❌ IOException while capturing screenshot: {}", e.getMessage());
            logger.debug("Full exception:", e);
            return null;

        } catch (ClassCastException e) {
            logger.error("❌ WebDriver does not support TakesScreenshot: {}", e.getMessage());
            return null;

        } catch (Exception e) {
            logger.error("❌ Unexpected error while capturing screenshot: {}", e.getMessage());
            logger.debug("Full exception:", e);
            return null;
        }
    }

    /**
     * Take screenshot and return as byte array (for Allure report attachment).
     * Does not save to disk, only returns the bytes in memory.
     *
     * @param driver the WebDriver instance
     * @return screenshot as byte array, or empty array if capture fails
     */
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        if (driver == null) {
            logger.warn("WebDriver is null, cannot capture screenshot as bytes");
            return new byte[0];
        }

        try {
            // Use TakesScreenshot to capture as bytes
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);

            logger.info("✅ Screenshot captured as bytes successfully");
            logger.debug("Screenshot size: {} bytes", screenshotBytes.length);

            return screenshotBytes;

        } catch (ClassCastException e) {
            logger.error("❌ WebDriver does not support TakesScreenshot: {}", e.getMessage());
            return new byte[0];

        } catch (Exception e) {
            logger.error("❌ Error while capturing screenshot as bytes: {}", e.getMessage());
            logger.debug("Full exception:", e);
            return new byte[0];
        }
    }

    /**
     * Get the screenshot directory path.
     * Useful for locating saved screenshots after tests complete.
     *
     * @return the screenshot directory path
     */
    public static String getScreenshotDir() {
        return SCREENSHOT_DIR;
    }

    /**
     * Delete all screenshots in the directory (cleanup).
     * Can be called after test suite completes if you want to clean up old screenshots.
     *
     * @return true if cleanup successful, false otherwise
     */
    public static boolean cleanupScreenshots() {
        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (screenshotDir.exists() && screenshotDir.isDirectory()) {
                File[] files = screenshotDir.listFiles();
                if (files != null) {
                    int deletedCount = 0;
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".png")) {
                            if (file.delete()) {
                                deletedCount++;
                            }
                        }
                    }
                    logger.info("✅ Cleanup complete: {} screenshots deleted", deletedCount);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("❌ Error during screenshot cleanup: {}", e.getMessage());
            return false;
        }
    }
}