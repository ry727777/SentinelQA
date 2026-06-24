package com.rahul.tests.base;

import com.rahul.framework.config.ConfigReader;
import com.rahul.framework.config.DriverManager;
import com.rahul.framework.utils.LoggerUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Base class for all UI test classes.
 * Handles WebDriver setup and teardown using DriverManager.
 */
public class BaseTest {

    protected static final Logger logger = LoggerUtil.getLogger(BaseTest.class);
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        logger.info("Setting up WebDriver for test");

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (System.getenv("CI") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getIntProperty("ui.timeout")));

        DriverManager.setDriver(driver);

        String baseUrl = ConfigReader.getProperty("base.url");
        driver.get(baseUrl);
        logger.info("Navigated to base URL: {}", baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Tearing down WebDriver");
        DriverManager.unload();
    }

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }
}
