package com.rahul.framework.pages;

import com.rahul.framework.utils.LoggerUtil;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage {
    private static final Logger logger = LoggerUtil.getLogger(CartPage.class);
    WebDriver driver;

    public CartPage(WebDriver driver){
        this.driver = driver;
        logger.info("Initializing CartPage");
    }

    private List<WebElement> itemList;

    // Locators
    private By cartLink = By.className("shopping_cart_link");
    private By invetoryList = By.className("inventory_item");
    private By cartPageItems = By.xpath("//span[@class=\"shopping_cart_badge\"]");
    private By addBtn = By.xpath(".//button[text()=\"Add to cart\"]");
    private By removebtn = By.xpath(".//button[text()=\"Remove\"]");

    public boolean checkItemsPresent(WebDriver driver){
        itemList = driver.findElements(invetoryList);
        logger.info("Number of inventory items present: {}", itemList.size());
        return itemList.size() != 0;
    }

    public int  numberOfElementsCart(WebDriver driver){
        int totalItems = Integer.parseInt(driver.findElement(cartPageItems).getText());
        logger.info("Number of items displayed in cart badge: {}", totalItems);
        return totalItems;
    }

    public boolean addToCart(WebDriver driver){
        logger.info("Adding items to cart");
        boolean items = checkItemsPresent(driver);
        if(!items) {
            logger.warn("No inventory items found to add to cart");
            return false;
        }
        // add one item to the cart

        for(int i=0; i<3; i++){
            WebElement addToCartBtn = itemList.get(i).findElement(addBtn);
            addToCartBtn.click();
            logger.info("Added item {} to cart", i + 1);
        }

        int total_items = numberOfElementsCart(driver);
        logger.info("Cart add validation result. Expected: 3, Actual: {}", total_items);

        return total_items == 3;
        
    }

    public boolean removeItems(WebDriver driver){
        logger.info("Removing items from cart");

        int beforeRemoved = numberOfElementsCart(driver);

        // remove one
        itemList.get(0).findElement(removebtn).click();
        logger.info("Removed first item from cart");

        int afterRemovedOne = numberOfElementsCart(driver);

        itemList.get(1).findElement(removebtn).click();
        logger.info("Removed second item from cart");

        int afterRemovedSecond = numberOfElementsCart(driver);
        logger.info("Cart remove validation result. Before: {}, After first remove: {}, After second remove: {}",
                beforeRemoved, afterRemovedOne, afterRemovedSecond);
        
        return beforeRemoved == 3 && afterRemovedOne == 2 && afterRemovedSecond == 1;
    }
}
