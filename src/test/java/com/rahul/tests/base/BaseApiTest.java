package com.rahul.tests.base;

import com.rahul.framework.config.ConfigReader;
import com.rahul.framework.utils.LoggerUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;

/**
 * Base class for all API test classes.
 * Handles REST Assured request specification setup.
 */
public class BaseApiTest {

    protected static final Logger logger = LoggerUtil.getLogger(BaseApiTest.class);
    protected static RequestSpecification requestSpec;

    @BeforeClass(alwaysRun = true)
    public void apiSetUp() {
        String apiBaseUrl = ConfigReader.getProperty("api.base.url");
        String apiKey = System.getenv("REQRES_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = ConfigReader.getProperty("api.key");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("ReqRes API key is missing. Set REQRES_API_KEY or api.key in config.properties.");
        }

        logger.info("Initializing REST Assured base URI: {}", apiBaseUrl);
        logger.info("ReqRes API key configured: true");

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(apiBaseUrl)
                .setContentType("application/json")
                .addHeader("x-api-key", apiKey)
                .addHeader("X-Reqres-Env", "prod")
                .build();
    }
}
