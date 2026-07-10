package com.rahul.tests.ui;

import com.rahul.framework.config.ConfigReader;
import com.rahul.framework.pages.LoginPage;
import com.rahul.framework.utils.LoggerUtil;
import com.rahul.tests.base.BaseTest;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * UI tests for the SauceDemo login page.
 * Extends BaseTest to inherit WebDriver setup/teardown.
 * URL: https://www.saucedemo.com
 */
public class LoginTest extends BaseTest {
    private static final Logger logger = LoggerUtil.getLogger(LoginTest.class);

    /**
     * Test valid login with correct credentials.
     * Expected: User is logged in and redirected to inventory page.
     */
    @Test(groups = "login")
    public void testValidLogin() {
        LoginPage loginpage = new LoginPage(driver);

        String user_name = ConfigReader.getProperty("username");
        String password = ConfigReader.getProperty("password");

        loginpage.login(user_name, password);

        String curr_url = driver.getCurrentUrl();
        Assert.assertTrue(curr_url.contains("inventor"), "Login failed"); // inventory -> inventor.

        logger.info("Testing valid login");
    }

    /**
     * Test invalid login with incorrect credentials.
     * Expected: Error message displayed, user not logged in.
     */
    @Test(groups = "login")
    public void testInvalidLogin() {
        LoginPage loginpage = new LoginPage(driver);

        String user_name = ConfigReader.getProperty("username");
        String password = "wrong_password";

        loginpage.login(user_name, password);

        String curr_url = driver.getCurrentUrl();

        Assert.assertFalse(curr_url.contains("inventory"), "Should not Login");

        String error_msg = loginpage.errorMessage();
        Assert.assertTrue(error_msg.contains("Username and password do not match"));
        logger.info("Testing invalid login");
    }
}
