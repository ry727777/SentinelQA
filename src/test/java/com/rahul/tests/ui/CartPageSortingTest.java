package com.rahul.tests.ui;

import com.rahul.framework.config.ConfigReader;
import com.rahul.framework.pages.*;
import com.rahul.framework.utils.LoggerUtil;
import com.rahul.tests.base.BaseTest;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import org.testng.Assert;

public class CartPageSortingTest extends BaseTest{
    private static final Logger logger = LoggerUtil.getLogger(CartPageSortingTest.class);
    
    @Test(groups = "low_to_high")
    public void increasingOrderTest() {
        logger.info("Starting cart price sorting test: low to high");
        LoginPage loginpage = new LoginPage(driver);

        String user_name = ConfigReader.getProperty("username");
        String password = ConfigReader.getProperty("password");
        loginpage.login(user_name, password);
        logger.info("Logged in successfully for low to high sorting test");

        CartPage cart_page = new CartPage(driver);
        boolean items_pre = cart_page.checkItemsPresent(driver);
        Assert.assertTrue(items_pre, "Items not present on cart page");
        logger.info("Verified items are present on cart page");

        // check items should come in increasing order
        SortingCartPageItems sortCart = new SortingCartPageItems(driver);
        boolean isSorted = sortCart.lowToHigh();
        Assert.assertTrue(isSorted, "Items are not sorted based on price low to high");
        logger.info("Verified cart items are sorted from low to high price");
      
    }

    @Test(groups = "high_to_low")
    public void decreasingOrderTest() {
        logger.info("Starting cart price sorting test: high to low");
        LoginPage loginpage = new LoginPage(driver);

        String user_name = ConfigReader.getProperty("username");
        String password = ConfigReader.getProperty("password");

        loginpage.login(user_name, password);
        logger.info("Logged in successfully for high to low sorting test");

        CartPage cart_page = new CartPage(driver);
        boolean items_pre = cart_page.checkItemsPresent(driver);
        Assert.assertTrue(items_pre, "Items not present on cart page");
        logger.info("Verified items are present on cart page");

        // check items should come in decreasing order
        SortingCartPageItems sortCart = new SortingCartPageItems(driver);
        boolean isSorted = sortCart.highTolow();
        Assert.assertTrue(isSorted, "Items are not sorted based on price high to low");
        logger.info("Verified cart items are sorted from high to low price");
    }
}
