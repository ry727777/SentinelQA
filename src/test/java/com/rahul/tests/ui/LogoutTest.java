package com.rahul.tests.ui;

import com.rahul.framework.config.ConfigReader;
import com.rahul.framework.pages.LoginPage;
import com.rahul.framework.pages.LogoutPage;
import com.rahul.framework.utils.LoggerUtil;
import com.rahul.tests.base.BaseTest;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import org.testng.Assert;

public class LogoutTest extends BaseTest{
    private static final Logger logger = LoggerUtil.getLogger(LoginTest.class);

    @Test(groups = "logout")
    public void testLogOut(){
        LoginPage loginpage = new LoginPage(driver);
        LogoutPage logoutpage = new LogoutPage(driver);

        String user_name = ConfigReader.getProperty("username");
        String password = ConfigReader.getProperty("password");

        // First Login
        loginpage.login(user_name, password);

        // logut
        logoutpage.logout();

        // verify if logout and comback to login page ?
        boolean login_page_validation = loginpage.loginPageValidation();
        Assert.assertTrue(login_page_validation, "Logout Failed");

        logger.info("Testing valid logout");
    }
}
