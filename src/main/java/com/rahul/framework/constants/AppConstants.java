package com.rahul.framework.constants;

/**
 * Application-wide constants.
 * Defines static final values for URLs, timeouts, and other configuration.
 */
public class AppConstants {

    // SauceDemo URLs
    public static final String SAUCEDEMO_BASE_URL = "https://www.saucedemo.com";
    public static final String SAUCEDEMO_LOGIN_PATH = "/";
    public static final String SAUCEDEMO_INVENTORY_PATH = "/inventory.html";

    // RESTful API URLs (reqres.in)
    public static final String REQRES_BASE_URL = "https://reqres.in";
    public static final String REQRES_LOGIN_ENDPOINT = "/api/login";
    public static final String REQRES_USERS_ENDPOINT = "/api/users";

    // Timeout values (in seconds)
    public static final int IMPLICIT_WAIT_SECONDS = 10;
    public static final int EXPLICIT_WAIT_SECONDS = 15;
    public static final int API_TIMEOUT_SECONDS = 5;

    // Browser configuration
    public static final String BROWSER_CHROME = "chrome";
    public static final String BROWSER_FIREFOX = "firefox";
    public static final String BROWSER_EDGE = "edge";

    // Test data
    public static final String VALID_USERNAME = "standard_user";
    public static final String VALID_PASSWORD = "secret_sauce";
    public static final String INVALID_USERNAME = "invalid_user";
    public static final String INVALID_PASSWORD = "invalid_pass";

    // Environment variables
    public static final String OPENAI_API_KEY_ENV = "OPENAI_API_KEY";
}
