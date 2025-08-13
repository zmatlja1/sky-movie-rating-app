package integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MovieStepDefinitions {

    private static final String LATEST_CREATED_RATING_ID = "lastId";
    private static final int port = 8080;

    private static final Map<String, Object> CONTEXT = new ConcurrentHashMap<>();

    private String username;
    private String password;
    private Response response;

    @Given("user {string} with password {string}")
    public void user_with_password(String user, String pass) {
        this.username = user;
        this.password = pass;
    }

    @When("the user sends GET request to {string}")
    public void send_get_request(String path) {
        response = RestAssured.given()
            .auth().basic(username, password)
            .port(port)
            .get(path);
    }

    @When("the user sends POST request to {string} with body:")
    public void send_post_request(String path, String body) {
        response = RestAssured.given()
            .auth().basic(username, password)
            .port(port)
            .contentType("application/json")
            .body(body)
            .post(path);

        var ratingId = response.getBody().jsonPath().get("id");

        CONTEXT.put(LATEST_CREATED_RATING_ID, ratingId);
    }

    @When("the user sends PUT request to {string} with body:")
    public void send_put_request(String path, String body) {
        response = RestAssured.given()
            .auth().basic(username, password)
            .port(port)
            .contentType("application/json")
            .body(body)
            .put(path + CONTEXT.get(LATEST_CREATED_RATING_ID));
    }

    @When("the user sends DELETE request to {string}")
    public void send_delete_request(String path) {
        response = RestAssured.given()
            .auth().basic(username, password)
            .port(port)
            .delete(path + CONTEXT.get(LATEST_CREATED_RATING_ID));
    }

    @Then("the response status should be {int}")
    public void response_status_should_be(int status) {
        assertEquals(status, response.getStatusCode());
    }

    @Then("the response status should be {int} and first movie name {} with rating {int}")
    public void response_status_should_be_and_movie_name_with_rating(int status, String name, int rating) {
        assertEquals(status, response.getStatusCode());

        var responseJsonPath = response.getBody().jsonPath();
        assertEquals(name, responseJsonPath.get("[0].name"));
        assertEquals(rating, (int)responseJsonPath.get("[0].rating"));
    }
}
