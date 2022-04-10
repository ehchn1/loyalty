package com.mycompany.loyalty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mycompany.loyalty.merchant.Merchant;
import com.mycompany.loyalty.offer.Offer;
import com.mycompany.loyalty.offer.OfferType;
import com.mycompany.loyalty.security.JwtRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class OfferApiTest {

    private static final String API_ROOT = "http://localhost:8080/loyalty/api/v1.0/offers";

    private static final String TOKEN_API = "http://localhost:8080/loyalty/authenticate";

    private static String token;

    private Offer createOffer() {
        Offer offer = new Offer();
        offer.setName(UUID.randomUUID().toString());
        offer.setDescription("desc");
        OfferType offerType = new OfferType();
        offerType.setId("c9abea17-bac2-44ee-9b31-ea8e02cadcbf");
        offer.setOfferType(offerType);
        offer.setPoints(100);
        offer.setCashRebate(10.2f);
        offer.setStartDate(LocalDate.now());
        offer.setEndDate(LocalDate.now().plusDays(2));
        Merchant merchant = new Merchant();
        merchant.setId("d4974e0b-b365-11ec-8d2f-c8f7506b53c1");
        offer.setMerchant(merchant);
        return offer;
    }

    private String createOfferUri(Offer offer) {
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(offer).post(API_ROOT);

        return API_ROOT + "/" + response.jsonPath().get("id");
    }

    @BeforeAll
    static void setup() {
        JwtRequest request = new JwtRequest("loyalty", "password");
        Response response = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request).post(TOKEN_API);
        token = response.jsonPath().get("token");
    }

    @Test
    void whenGetAllOffers_thenOK() {
        Map<String, String> map = new HashMap<>();
        map.put("pageNo", "0");
        map.put("pageSize", "10");
        map.put("date", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .queryParams(map).get(API_ROOT);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    void whenGetCreatedOfferById_thenOK() {
        Offer offer = createOffer();
        String uri = createOfferUri(offer);
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .get(uri);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(offer.getName(), response.jsonPath().get("name"));
    }

    @Test
    void whenGetNotExistOfferById_thenNotFound() {
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .get(API_ROOT + "/invalid-id");

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    void whenCreateNewOffer_thenCreated() {
        Offer offer = createOffer();
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(offer)
                .post(API_ROOT);

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
    }

    @Test
    void whenUpdateCreatedOffer_thenUpdated() {
        Offer offer = createOffer();
        String location = createOfferUri(offer);
        offer.setId(location.split("api/v1.0/offers/")[1]);
        offer.setName("new-name");
        Response response = RestAssured.given()
                .auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(offer)
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
    void whenDeleteCreatedOffer_thenOk() {
        Offer offer = createOffer();
        String location = createOfferUri(offer);
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
