package com.rahul.tests.api;

import com.rahul.framework.constants.AppConstants;
import com.rahul.framework.utils.LoggerUtil;
import com.rahul.tests.base.BaseApiTest;

import io.restassured.response.Response;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API tests for user CRUD operations.
 * Extends BaseApiTest to inherit REST Assured configuration.
 * Tests Create, Read, Update, Delete operations on users.
 * Target: https://reqres.in
 */
public class UserApiTest extends BaseApiTest {
    private static final Logger logger = LoggerUtil.getLogger(UserApiTest.class);
    private static int createdUserId;

    /**
     * Test create user endpoint.
     * Expected: Status code 201 (Created) with user id, name, job, createdAt timestamp
     * POST /api/users with name and job
     */
    @Test(priority = 1)
    public void testCreateUser() {
        logger.info("Testing API - Create User endpoint");

        // Create request body with user details
        String requestBody = "{\n" +
                "  \"name\": \"Rahul Yadav\",\n" +
                "  \"job\": \"SDET Engineer\"\n" +
                "}";

        logger.info("Request body: {}", requestBody);

        // Send POST request to /api/users endpoint
        Response response = given()
                .spec(requestSpec)
                .body(requestBody)
        .when()
                .post(AppConstants.REQRES_USERS_ENDPOINT)
        .then()
                .statusCode(201)
                .body("name", equalTo("Rahul Yadav"))
                .body("job", equalTo("SDET Engineer"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract()
                .response();

        // Extract user details
        createdUserId = response.jsonPath().getInt("id");
        String name = response.jsonPath().getString("name");
        String job = response.jsonPath().getString("job");
        String createdAt = response.jsonPath().getString("createdAt");

        logger.info("User created successfully!");
        logger.info("Created User ID: {}", createdUserId);
        logger.info("Name: {}", name);
        logger.info("Job: {}", job);
        logger.info("Created At: {}", createdAt);
        logger.info("Response body: {}", response.prettyPrint());
    }

    /**
     * Test get single user by ID.
     * Expected: Status code 200 with user details matching the requested ID
     * GET /api/users/{id}
     */
    @Test(priority = 2)
    public void testGetSingleUserById() {
        logger.info("Testing API - Get Single User by ID");

        // Using a known user ID from reqres.in (ID 2)
        int userId = 2;
        String endpoint = AppConstants.REQRES_USERS_ENDPOINT + "/" + userId;

        logger.info("Fetching user with ID: {}", userId);

        // Send GET request to /api/users/{id} endpoint
        Response response = given()
                .spec(requestSpec)
        .when()
                .get(endpoint)
        .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data.id", equalTo(userId))
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract()
                .response();

        // Extract user details
        int id = response.jsonPath().getInt("data.id");
        String email = response.jsonPath().getString("data.email");
        String firstName = response.jsonPath().getString("data.first_name");
        String lastName = response.jsonPath().getString("data.last_name");
        String avatar = response.jsonPath().getString("data.avatar");

        logger.info("User retrieved successfully!");
        logger.info("User ID: {}", id);
        logger.info("Email: {}", email);
        logger.info("Name: {} {}", firstName, lastName);
        logger.info("Avatar: {}", avatar);
        logger.info("Response body: {}", response.prettyPrint());
    }

    /**
     * Test update user endpoint.
     * Expected: Status code 200 with updated user details and updatedAt timestamp
     * PUT /api/users/{id} with name and job
     */
    @Test(priority = 3)
    public void testUpdateUser() {
        logger.info("Testing API - Update User endpoint");

        // Use user ID 3 for this test
        int userId = 3;
        String endpoint = AppConstants.REQRES_USERS_ENDPOINT + "/" + userId;

        // Create request body with updated user details
        String requestBody = "{\n" +
                "  \"name\": \"Rahul Updated\",\n" +
                "  \"job\": \"Senior SDET Engineer\"\n" +
                "}";

        logger.info("Updating user ID: {}", userId);
        logger.info("Request body: {}", requestBody);

        // Send PUT request to /api/users/{id} endpoint
        Response response = given()
                .spec(requestSpec)
                .body(requestBody)
        .when()
                .put(endpoint)
        .then()
                .statusCode(200)
                .body("name", equalTo("Rahul Updated"))
                .body("job", equalTo("Senior SDET Engineer"))
                .body("updatedAt", notNullValue())
                .extract()
                .response();

        // Extract updated user details
        String name = response.jsonPath().getString("name");
        String job = response.jsonPath().getString("job");
        String updatedAt = response.jsonPath().getString("updatedAt");

        logger.info("User updated successfully!");
        logger.info("Updated Name: {}", name);
        logger.info("Updated Job: {}", job);
        logger.info("Updated At: {}", updatedAt);
        logger.info("Response body: {}", response.prettyPrint());
    }

    /**
     * Test delete user endpoint.
     * Expected: Status code 204 (No Content) with empty response body
     * DELETE /api/users/{id}
     */
    @Test(priority = 4)
    public void testDeleteUser() {
        logger.info("Testing API - Delete User endpoint");

        // Use user ID 2 for deletion
        int userId = 2;
        String endpoint = AppConstants.REQRES_USERS_ENDPOINT + "/" + userId;

        logger.info("Deleting user ID: {}", userId);

        // Send DELETE request to /api/users/{id} endpoint
        Response response = given()
                .spec(requestSpec)
        .when()
                .delete(endpoint)
        .then()
                .statusCode(204)
                .extract()
                .response();

        logger.info("User deleted successfully!");
        logger.info("Status Code: 204 (No Content)");
        logger.info("Response body is empty (as expected for 204)");
        logger.info("Response body: '{}'", response.prettyPrint());
    }

    /**
     * Test get users with pagination.
     * Expected: Status code 200 with multiple pages of users
     * GET /api/users?page=2
     */
    @Test(priority = 5)
    public void testGetUsersWithPagination() {
        logger.info("Testing API - Get Users with Pagination");

        // Request page 2 of users
        int pageNumber = 2;

        logger.info("Fetching users page: {}", pageNumber);

        // Send GET request to /api/users with page query parameter
        Response response = given()
                .spec(requestSpec)
                .queryParam("page", pageNumber)
        .when()
                .get(AppConstants.REQRES_USERS_ENDPOINT)
        .then()
                .statusCode(200)
                .body("page", equalTo(pageNumber))
                .body("per_page", notNullValue())
                .body("total", greaterThan(0))
                .body("total_pages", greaterThan(1))
                .body("data", notNullValue())
                .body("data.size()", greaterThan(0))
                .extract()
                .response();

        // Extract pagination details
        int page = response.jsonPath().getInt("page");
        int perPage = response.jsonPath().getInt("per_page");
        int total = response.jsonPath().getInt("total");
        int totalPages = response.jsonPath().getInt("total_pages");
        int dataSize = response.jsonPath().getList("data").size();

        logger.info("Pagination details retrieved successfully!");
        logger.info("Current Page: {}", page);
        logger.info("Per Page: {}", perPage);
        logger.info("Total Users: {}", total);
        logger.info("Total Pages: {}", totalPages);
        logger.info("Users on this page: {}", dataSize);
        logger.info("Response body: {}", response.prettyPrint());
    }
}
