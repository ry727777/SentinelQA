package com.rahul.framework.pages;

import com.rahul.framework.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class SortingCartPageItems {

    WebDriver driver;

    By sortingEle = By.className("product_sort_container");
    By itemsPrice = By.className("inventory_item_price");
    private static final Logger logger = LoggerUtil.getLogger(SortingCartPageItems.class);

    public SortingCartPageItems(WebDriver driver) {
        this.driver = driver;
        logger.info("Initializing SortingCartPageItems");
    }

    boolean isSorted(List<Double> priceValues, boolean asc){
        logger.info("Checking if {} prices are sorted in {} order", priceValues.size(), asc ? "ascending" : "descending");
        if(asc){
            for(int i=0; i<priceValues.size()-1; i++){
                if(priceValues.get(i) > priceValues.get(i+1)) {
                    logger.warn("Price order mismatch at index {}: {} > {}", i, priceValues.get(i), priceValues.get(i + 1));
                    return false;
                }
            }
        }else{
             for(int i=0; i<priceValues.size()-1; i++){
                if(priceValues.get(i) < priceValues.get(i+1)) {
                    logger.warn("Price order mismatch at index {}: {} < {}", i, priceValues.get(i), priceValues.get(i + 1));
                    return false;
                }
            }
        }
        logger.info("Price list is sorted successfully");
        return true;
    }

    public List<Double> fethPrice(List<WebElement> itemsList){
        logger.info("Fetching prices from {} inventory items", itemsList.size());
        List<Double> ele = new ArrayList<>();
        for(int i=0; i<itemsList.size(); i++){
            WebElement t = itemsList.get(i);
            String s = t.getText();
            Double num = Double.parseDouble(s.replace("$", "").trim());
            ele.add(num);
        }
        logger.info("Fetched item prices: {}", ele);
        return ele;
    }


    public boolean lowToHigh() {
        logger.info("Selecting price sort option: low to high");
        WebElement dropdown = driver.findElement(sortingEle);
        Select select = new Select(dropdown);
        select.selectByVisibleText("Price (low to high)");

        // verify items are in sorted order low to high
        List<WebElement> itemsList = driver.findElements(itemsPrice);

        List<Double> ele = fethPrice(itemsList);
        boolean result = isSorted(ele, true);
        logger.info("Low to high sorting validation result: {}", result);
        return result;

    }

    public boolean highTolow() {
        logger.info("Selecting price sort option: high to low");
        WebElement dropdown = driver.findElement(sortingEle);
        Select select = new Select(dropdown);
        select.selectByVisibleText("Price (high to low)");

        // verify items are in sorted order low to high
        List<WebElement> itemsList = driver.findElements(itemsPrice);

        List<Double> ele = fethPrice(itemsList);
        boolean result = isSorted(ele, false);
        logger.info("High to low sorting validation result: {}", result);
        return result;
    }
}
