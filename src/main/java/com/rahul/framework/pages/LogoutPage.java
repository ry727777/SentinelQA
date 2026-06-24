package com.rahul.framework.pages;
import com.rahul.framework.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LogoutPage {
    WebDriver driver;
    private static final Logger logger = LoggerUtil.getLogger(LoginPage.class);

    By right_button = By.id("react-burger-menu-btn");
    By logoutbtn = By.id("logout_sidebar_link");

    public LogoutPage(WebDriver driver){
        this.driver = driver;
    }

    public void logout(){
        driver.findElement(right_button).click();
        logger.info("Clicking logout button");
        driver.findElement(logoutbtn).click();
        logger.info("Logged Out");
    }
}
