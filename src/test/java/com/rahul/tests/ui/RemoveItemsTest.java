package com.rahul.tests.ui;

import com.rahul.framework.config.ConfigReader;
import com.rahul.framework.pages.*;
import com.rahul.framework.utils.LoggerUtil;
import com.rahul.tests.base.BaseTest;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import org.testng.Assert;

public class RemoveItemsTest extends BaseTest {
    private static final Logger logger = LoggerUtil.getLogger(RemoveItemsTest.class);
    
    @Test(groups = "remove_from_cart")
    public void testRemoveFromCart(){
        logger.info("Starting remove items from cart test");
        LoginPage loginpage = new LoginPage(driver);

        String user_name = ConfigReader.getProperty("username");
        String password = ConfigReader.getProperty("password");

        loginpage.login(user_name, password);
        logger.info("Logged in successfully for remove items test");

        // Add element to the cart page
        CartPage cart_page = new CartPage(driver);
        boolean result = cart_page.addToCart(driver);
        Assert.assertTrue(result, "Add to cart failed");
        logger.info("Verified items were added to cart");


        // remove element
        boolean result2 = cart_page.removeItems(driver);
        Assert.assertTrue(result2, "Items not removed from cart page");
        logger.info("Verified items were removed from cart");

    }
}
