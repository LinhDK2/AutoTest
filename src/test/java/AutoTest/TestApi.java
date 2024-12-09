package AutoTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TestApi {

    private static final String API_BASE_URL = "http://localhost:3000";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = API_BASE_URL;
    }

    @Test
    public void createUserTest() {
        String requestBody = "{ \"username\": \"testuser\", \"password\": \"password123\", \"email\": \"testuser@example.com\", \"fullName\": \"Test User\" }";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("username", equalTo("testuser"))
                .body("email", equalTo("testuser@example.com"))
                .extract()
                .response();

        String userId = response.jsonPath().getString("id");
        System.out.println("User ID: " + userId);
    }

    @Test
    public void searchProductByCategoryTest() {
        Response response = given()
                .queryParam("category", "Thiết bị điện tử")
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .extract()
                .response();

        String productId = response.jsonPath().getString("[0].id");
        System.out.println("Product ID: " + productId);
    }

    @Test
    public void placeOrderTest() {
        String orderRequestBody = "{ \"userId\": \"3\", \"status\": \"processing\", \"items\": [{ \"productId\": \"4\", \"quantity\": 1 }] }";

        given()
                .header("Content-Type", "application/json")
                .body(orderRequestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    public void writeReviewTest() {
        String reviewRequestBody = "{ \"userId\": \"3\", \"productId\": \"4\", \"rating\": 5, \"comment\": \"Sản phẩm quá tuyệt vời!\" }";

        given()
                .header("Content-Type", "application/json")
                .body(reviewRequestBody)
                .when()
                .post("/reviews")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

}
