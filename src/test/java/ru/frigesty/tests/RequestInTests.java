package ru.frigesty.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class RequestInTests {

    @BeforeEach
    public void beforeEach(){
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    void correctDataInPageListUsers() {
        given()
                .when()
                .get("/users?page=2")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemes/listUsersScheme.json"))
                .body("data.id", hasItems(7, 8, 9, 10, 11, 12))
                .assertThat().body("data[0].email", is("michael.lawson@reqres.in"),
                   "data[0].first_name", is("Michael"),
                                        "data[0].last_name", is("Lawson"),
                                        "data[0].avatar", is("https://reqres.in/img/faces/7-image.jpg"));
    }

    @Test
    void correctDataInPageSingleUser() {
        given()
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemes/singleUserScheme.json"))
                .assertThat().body("data.id", is(2),
                        "data.email", is("janet.weaver@reqres.in"),
                                             "data.first_name", is("Janet"),
                                             "data.last_name", is("Weaver"),
                                             "data.avatar", is("https://reqres.in/img/faces/2-image.jpg"));
    }

    @Test
    void pageSingleUserNotFound() {
        given()
                .when()
                .get("/users/23")
                .then()
                .statusCode(404)
                .log().status()
                .log().body();
    }

    @Test
    void correctDataInPageListResource() {
        given()
                .when()
                .get("/unknown")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemes/listResourceScheme.json"))
                .body("data.id", hasItems(1, 2, 3, 4, 5, 6))
                .assertThat().body("data.id[0]", is(1),
                        "data.name[0]", is("cerulean"),
                        "data.year[0]", is(2000),
                        "data.color[0]", is("#98B2D1"),
                        "data.pantone_value[0]", is("15-4020"));
    }

    @Test
    void correctDataInPageSingleResource() {
        given()
                .when()
                .get("unknown/2")
                .then()
                .statusCode(200)
                .log().body()
                .body(matchesJsonSchemaInClasspath("schemes/singleResourceScheme.json"))
                .assertThat().body("data.id", is(2),
                        "data.name", is("fuchsia rose"),
                        "data.year", is(2001),
                        "data.color", is("#C74375"),
                        "data.pantone_value", is("17-2031"));
    }

    @Test
    void pageSingleResourceNotFound() {
        given()
                .when()
                .get("/unknown/23")
                .then()
                .statusCode(404);
    }

    @Test
    void successfulCreateTest() {

        String requestBody = "{ \"name\": \"morpheus\", \"job\": \"leader\" }";

        given()
            .log().uri()
            .log().body()
            .contentType(JSON)
            .body(requestBody)
            .when()
            .post("/users?page=2")
            .then()
            .statusCode(201)
            .body(matchesJsonSchemaInClasspath("schemes/createUserScheme.json"))
            .assertThat().body("name", is("morpheus"),
                        "job", is("leader"));
    }

    @Test
    void successfulLoginTest() {
        String requestBody = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }"; // BAD PRACTICE

        given()
                .log().uri()
                .log().body()
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    void negativeLogin400Test() {
        String requestBody = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }"; // BAD PRACTICE

        given()
                .log().uri()
                .log().body()
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing email or username"));
    }

    @Test
    void negativeLoginTest() {
        given()
                .log().uri()
                .log().body()
                .when()
                .post("/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(415);
    }
}