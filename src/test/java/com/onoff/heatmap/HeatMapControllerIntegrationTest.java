package com.onoff.heatmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onoff.heatmap.controllers.response.SuccessResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HeatMapControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    private final String baseUrl = "http://localhost";
    private URI uri;

    @Test
    void whenWrongCredentials_thenReturns401() throws URISyntaxException {
        restTemplate = new TestRestTemplate().withBasicAuth("admin", "admin");
        uri = new URI(baseUrl + ":" + port + "/api/heatmap/answer-rate?dateInput=2024-04-10&numberOfShades=7&startHour=0&endHour=23");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate.getForEntity(uri, SuccessResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void whenMissingNumberOfShades_thenReturns400() throws URISyntaxException {
        restTemplate = new TestRestTemplate().withBasicAuth("admin", "admin123");
        uri = new URI(baseUrl + ":" + port + "/api/heatmap/answer-rate?dateInput=2024-04-10");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate.getForEntity(uri, SuccessResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenValidRequest_thenReturns200() throws URISyntaxException, JsonProcessingException {
        restTemplate = new TestRestTemplate().withBasicAuth("admin", "admin123");
        uri = new URI(baseUrl + ":" + port + "/api/heatmap/answer-rate?dateInput=2024-04-10&numberOfShades=7");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate.getForEntity(uri, SuccessResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Object jsonObj = responseEntity.getBody();
        assertThat(jsonObj).isInstanceOf(SuccessResponse.class);
        String jsonStr = mapper.writeValueAsString(jsonObj);
        SuccessResponse<?> successResponse = mapper.readValue(jsonStr, SuccessResponse.class);
        System.out.println(successResponse);
    }

    @Test
    void whenValidRequestMatchingExistingDates_thenReturns200WithData() throws URISyntaxException, JsonProcessingException {
        restTemplate = new TestRestTemplate().withBasicAuth("admin", "admin123");
        uri = new URI(baseUrl + ":" + port + "/api/heatmap/answer-rate?dateInput=2025-04-10&numberOfShades=7");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate.getForEntity(uri, SuccessResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Object jsonObj = responseEntity.getBody();
        assertThat(jsonObj).isInstanceOf(SuccessResponse.class);
        String jsonStr = mapper.writeValueAsString(jsonObj);
        SuccessResponse<?> successResponse = mapper.readValue(jsonStr, SuccessResponse.class);
        System.out.println(successResponse);
    }
}
