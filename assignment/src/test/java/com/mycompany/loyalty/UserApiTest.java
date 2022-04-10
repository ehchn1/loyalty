package com.mycompany.loyalty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.mycompany.loyalty.merchant.Merchant;
import com.mycompany.loyalty.security.JwtRequest;
import com.mycompany.loyalty.user.User;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class UserApiTest {

    private static final String API_ROOT = "http://localhost:8080/loyalty/api/v1.0/users";

    private static final String TOKEN_API = "http://localhost:8080/loyalty/authenticate";

    private static String token;

    private User createUser() {
        User user = new User();
        user.setLoginName(UUID.randomUUID().toString());
        user.setFirstName("firstname");
        user.setLastName("lastname");

        Merchant merchant = new Merchant();
        merchant.setId("d4974e0b-b365-11ec-8d2f-c8f7506b53c1");
        user.setMerchants(Set.of(merchant));
        return user;
    }

    private String createUserUri(User user) {
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(user).post(API_ROOT);
        return API_ROOT + "/" + response.jsonPath().get("id");
    }

    @BeforeAll
    static void setup() {
        JwtRequest request = new JwtRequest("loyalty", "password");
        Response response = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request).post(TOKEN_API);
        token = response.jsonPath().get("token");
    }

    @Test
    void whenGetAllUsers_thenOK() {
        Map<String, String> map = new HashMap<>();
        map.put("pageNo", "0");
        map.put("pageSize", "10");
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .queryParams(map).get(API_ROOT);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    void whenGetCreatedUserById_thenOK() {
        User user = createUser();
        String uri = createUserUri(user);
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .get(uri);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(user.getLoginName(), response.jsonPath().get("loginName"));
    }

    @Test
    void whenGetNotExistUserById_thenNotFound() {
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .get(API_ROOT + "/invalid-id");

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    void whenCreateNewUser_thenCreated() {
        User user = createUser();
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(user)
                .post(API_ROOT);

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
    }

    @Test
    void whenUpdateCreatedUser_thenUpdated() {
        User user = createUser();
        String location = createUserUri(user);
        user.setId(location.split("api/v1.0/users/")[1]);
        user.setLoginName("new-login-name");
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(user)
                .put(location);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        response = RestAssured.given()
                .auth().oauth2(token)
                .get(location);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals("new-login-name", response.jsonPath()
                .get("loginName"));
    }

    @Test
    void whenDeleteCreatedUser_thenOk() {
        User user = createUser();
        String location = createUserUri(user);
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .delete(location);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        response = RestAssured.given()
                .auth().oauth2(token)
                .get(location);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }
}
