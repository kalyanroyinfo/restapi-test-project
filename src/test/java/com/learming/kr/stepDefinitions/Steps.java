package com.learming.kr.stepDefinitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learming.kr.requestBody.AuthorizationRequest;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class Steps {
    private static final String USER_ID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";
    private static final String USERNAME = "TOOLSQA-Test";
    private static final String PASSWORD = "Test@@123";
    private static final String BASE_URL = "https://bookstore.toolsqa.com";

    private static String token;
    private static Response response;
    private static String jsonString;
    private static String bookId;
    private static RequestSpecification request;
    static {
        try{
            RestAssured.baseURI = BASE_URL;
            request = RestAssured.given();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    @Given("I am an authorized user")
    public void i_am_an_authorized_user() throws JsonProcessingException {

        AuthorizationRequest authRequest = new AuthorizationRequest("TOOLSQA-Test","Test@@123");
        ObjectMapper objectMapper=new ObjectMapper();
        request.header("Content-Type", "application/json");
        String json=objectMapper.writeValueAsString(authRequest);
        response = request.body(json)
                .post("/Account/v1/GenerateToken");

        String jsonString = response.asString();
        token = JsonPath.from(jsonString).get("token");
    }

    @Given("A list of books are available")
    public void a_list_of_books_are_available() {

        response = request.get("/BookStore/v1/Books");

        jsonString = response.asString();
        List<Map<String, String>> books = JsonPath.from(jsonString).get("books");
        Assert.assertTrue(books.size() > 0);

        bookId = books.get(0).get("isbn");
    }

    @When("I add a book to my reading list")
    public void i_add_a_book_to_my_reading_list() {
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        response = request.body("{ \"userId\": \"" + USER_ID + "\", " +
                "\"collectionOfIsbns\": [ { \"isbn\": \"" + bookId + "\" } ]}")
                .post("/BookStore/v1/Books");
    }

    @Then("the book is added")
    public void the_book_is_added() {
        Assert.assertEquals(201, response.getStatusCode());
    }

    @When("I remove a book from my reading list")
    public void i_remove_a_book_from_my_reading_list() {
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        response = request.body("{ \"isbn\": \"" + bookId + "\", \"userId\": \"" + USER_ID + "\"}")
                .delete("/BookStore/v1/Book");
    }

    @Then("the book is removed")
    public void the_book_is_removed() {
        Assert.assertEquals(204, response.getStatusCode());


        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        response = request.get("/Account/v1/User/" + USER_ID);
        Assert.assertEquals(200, response.getStatusCode());

        jsonString = response.asString();
        List<Map<String, String>> booksOfUser = JsonPath.from(jsonString).get("books");
        Assert.assertEquals(0, booksOfUser.size());
    }
}
