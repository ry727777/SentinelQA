package com.rahul.framework.pages;

import com.rahul.framework.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object Model class for SauceDemo login page.
 * Encapsulates all UI elements and interactions for the login page.
 * URL: https://www.saucedemo.com
 */
public class LoginPage {
    private static final Logger logger = LoggerUtil.getLogger(LoginPage.class);
    private final WebDriver driver;

    private By username = By.id("username"); // change back to user-name
    private By password = By.id("password");
    private By  loggingBtn = By.id("login-button");

    private By app_logo = By.className("app_logo");
    private By product_text = By.xpath("//span[@class=\"title\"]");

    private By error_mess = By.xpath("//h3[@data-test=\"error\"]");

    /**
     * Constructor to initialize LoginPage with WebDriver instance.
     *
     * @param driver WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        logger.info("Initializing LoginPage");
    }

   
    public void enterUsername(String user_name) {
        driver.findElement(username).sendKeys(user_name);
        logger.info("Entering username: " + username);
    }

    
    public void enterPassword(String pass) {
        driver.findElement(password).sendKeys(pass);
        logger.info("Entering password");
    }

   
    public void clickLogin() {
        driver.findElement(loggingBtn).click();
        logger.info("Clicking login button");
    }

   
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        logger.info("Performing login with username: " + username);
    }

    public String errorMessage(){
        return driver.findElement(error_mess).getText();
    }

    public boolean dashboardPage(){
        String logo = driver.findElement(app_logo).getText();
        String product = driver.findElement(product_text).getText();
        return logo.equals("Swag Labs") && product.equals("Products");
    }

    public boolean loginPageValidation(){
        boolean t = driver.findElement(username).isDisplayed();
        boolean m = driver.findElement(password).isDisplayed();
        boolean n = driver.findElement(loggingBtn).isDisplayed();

        return t && m && n;
    }
}
