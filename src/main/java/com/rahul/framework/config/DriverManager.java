package com.rahul.framework.config;

import com.rahul.framework.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Thread-safe WebDriver manager using ThreadLocal.
 * Enables parallel test execution with isolated driver instances per thread.
 */
public class DriverManager {
    private static final Logger logger = LoggerUtil.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Get the WebDriver instance for the current thread.
     *
     * @return WebDriver instance or null if not set
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Set the WebDriver instance for the current thread.
     *
     * @param driver WebDriver instance to store
     */
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
        logger.info("WebDriver assigned to thread: {}", Thread.currentThread().getId());
    }

    /**
     * Remove the WebDriver instance from the current thread.
     * Should be called after test completion to free resources.
     */
    public static void unload() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            logger.info("WebDriver quit and removed for thread: {}", Thread.currentThread().getId());
        } else {
            logger.warn("unload() called but no WebDriver was set for thread: {}", Thread.currentThread().getId());
        }
    }

    /**
     * Check if a driver is currently set for this thread.
     *
     * @return true if driver is set, false otherwise
     */
    public static boolean isDriverSet() {
        return driverThreadLocal.get() != null;
    }
}