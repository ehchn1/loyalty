package com.mycompany.loyalty;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mycompany.loyalty.merchant.Merchant;
import com.mycompany.loyalty.security.JwtRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class MerchantApiTest {

    private static final String API_ROOT = "http://localhost:8080/loyalty/api/v1.0/merchants";

    private static final String TOKEN_API = "http://localhost:8080/loyalty/authenticate";

    private static String token;

    private Merchant createMerchant() {
        Merchant merchant = new Merchant();
        merchant.setName(UUID.randomUUID().toString());

        return merchant;
    }

    private String createMerchantUri(Merchant merchant) {
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(merchant).post(API_ROOT);
        return API_ROOT + "/" + response.jsonPath().get("id");
    }

    @BeforeAll
    static void setup() {
        JwtRequest request = new JwtRequest("loyalty", "password");
        Response response = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request).post(TOKEN_API);
        token = response.jsonPath().get("token");
    }

    @Test
    void whenGetAllMerchants_thenOK() {
        Map<String, String> map = new HashMap<>();
        map.put("pageNo", "0");
        map.put("pageSize", "10");
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .queryParams(map).get(API_ROOT);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    void whenGetCreatedMerchantById_thenOK() {
        Merchant merchant = createMerchant();
        String uri = createMerchantUri(merchant);
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .get(uri);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(merchant.getName(), response.jsonPath().get("name"));
    }

    @Test
    void whenGetNotExistMerchantById_thenNotFound() {
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .get(API_ROOT + "/invalid-id");

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    void whenCreateNewMerchant_thenCreated() {
        Merchant merchant = createMerchant();
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(merchant)
                .post(API_ROOT);

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
    }

    @Test
    void whenUpdateCreatedMerchant_thenUpdated() {
        Merchant merchant = createMerchant();
        String location = createMerchantUri(merchant);
        merchant.setId(location.split("api/v1.0/merchants/")[1]);
        merchant.setName("new-name");
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(merchant)
                .put(location);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        response = RestAssured.given()
                .auth().oauth2(token)
                .get(location);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals("new-name", response.jsonPath()
                .get("name"));
    }

    @Test
    void whenDeleteCreatedMerchant_thenOk() {
        Merchant merchant = createMerchant();
        String location = createMerchantUri(merchant);
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
