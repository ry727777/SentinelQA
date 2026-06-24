package com.rahul.tests.ui;

import com.rahul.framework.config.ConfigReader;
import com.rahul.framework.pages.*;
import com.rahul.framework.utils.LoggerUtil;
import com.rahul.tests.base.BaseTest;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import org.testng.Assert;

public class AddToCartTest extends BaseTest {
    private static final Logger logger = LoggerUtil.getLogger(LoginTest.class);

    @Test(groups = "add_to_cart")
    public void addToCartTest(){

        LoginPage loginpage = new LoginPage(driver);

        String user_name = ConfigReader.getProperty("username");
        String password = ConfigReader.getProperty("password");

        loginpage.login(user_name, password);

        // Add element to the cart page
        CartPage cart_page = new CartPage(driver);
        boolean result = cart_page.addToCart(driver);
        Assert.assertTrue(result, "Add to cart failed");

        logger.info("Validate Add to cart testing");
    }
}
