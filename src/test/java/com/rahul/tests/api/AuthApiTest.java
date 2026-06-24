package com.rahul.tests.api;

import com.rahul.framework.utils.LoggerUtil;
import com.rahul.tests.base.BaseApiTest;

import io.restassured.response.Response;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API tests for authentication endpoints.
 * Extends BaseApiTest to inherit REST Assured configuration.
 * Target: https://reqres.in
 */
public class AuthApiTest extends BaseApiTest {
    private static final Logger logger = LoggerUtil.getLogger(AuthApiTest.class);

    /**
     * Test login endpoint on RESTful API (reqres.in).
     * Expected: Status code 200 with token in response.
     * POST /api/login with email and password.
     */
    @Test(groups = "loginapi")
    public void testLoginEndpoint() {
        logger.info("Testing API login endpoint with valid credentials");

        // Create request body with email and password
        String requestBody = "{\n" +
                "  \"email\": \"eve.holt@reqres.in\",\n" +
                "  \"password\": \"cityslicka\"\n" +
                "}";

        logger.info("Request body: {}", requestBody);

        // Send POST request to /api/login endpoint and capture response
        Response response = given()
                .spec(requestSpec)
                .body(requestBody)
        .when()
                .post("/api/login")
        .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();

        // Extract and log the token
        String token = response.jsonPath().getString("token");
        logger.info("Login successful! Token received: {}", token);
        logger.info("Response body: {}", response.prettyPrint());
    }

    /**
     * Test login with invalid credentials.
     * Expected: Status code 400 with error message.
     */
    @Test(groups = "loginapi")
    public void testLoginWithInvalidCredentials() {
        logger.info("Testing API login with invalid credentials");

        // Create request body with invalid email/password
        String requestBody = "{\n" +
                "  \"email\": \"invalid@test.com\",\n" +
                "  \"password\": \"\"\n" +
                "}";

        logger.info("Request body: {}", requestBody);

        // Send POST request to /api/login endpoint
        Response response = given()
                .spec(requestSpec)
                .body(requestBody)
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("error", notNullValue())
                .extract()
                .response();

        // Extract and log the error message
        String errorMessage = response.jsonPath().getString("error");
        logger.info("Expected error received: {}", errorMessage);
        logger.info("Response body: {}", response.prettyPrint());
    }

    /**
     * Test get users endpoint.
     * Expected: Status code 200 with list of users.
     * GET /api/users
     */
    @Test(groups = "loginapi")
    public void testGetUsersEndpoint() {
        logger.info("Testing API get users endpoint");

        // Send GET request to /api/users endpoint
        Response response = given()
                .spec(requestSpec)
                .queryParam("page", 1)
        .when()
                .get("/api/users")
        .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data.size()", greaterThan(0))
                .body("page", equalTo(1))
                .body("total_pages", notNullValue())
                .extract()
                .response();

        // Extract user count and details
        int userCount = response.jsonPath().getInt("data.size()");
        int totalPages = response.jsonPath().getInt("total_pages");
        String firstUserEmail = response.jsonPath().getString("data[0].email");
        String firstUserName = response.jsonPath().getString("data[0].first_name");

        logger.info("Users retrieved successfully!");
        logger.info("Total users on page 1: {}", userCount);
        logger.info("Total pages: {}", totalPages);
        logger.info("First user: {} ({})", firstUserName, firstUserEmail);
        logger.info("Response body: {}", response.prettyPrint());
    }
}
