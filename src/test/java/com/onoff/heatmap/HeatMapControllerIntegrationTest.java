package com.onoff.heatmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onoff.heatmap.controllers.response.SuccessResponse;
import com.onoff.heatmap.models.HourlyCallStatsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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
    void whenValidRequestMatchingNoDates_thenReturns200WithDefaultValues() throws URISyntaxException, JsonProcessingException {
        restTemplate = new TestRestTemplate().withBasicAuth("admin", "admin123");
        uri = new URI(baseUrl + ":" + port + "/api/heatmap/answer-rate?dateInput=2024-04-10&numberOfShades=7");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate.getForEntity(uri, SuccessResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        String jsonStr = mapper.writeValueAsString(responseEntity.getBody());
        SuccessResponse<?> successResponse = mapper.readValue(jsonStr, SuccessResponse.class);
        assertThat(successResponse.getData()).isNotNull();
        List<HourlyCallStatsDto> hourlyStats = extractHourlyCallStatsFromResponse(successResponse);
        assertThat(hourlyStats).isNotEmpty();
        int sum = aggregateTotalCalls(hourlyStats);
        assertThat(sum).isEqualTo(0);
    }

    private static int aggregateTotalCalls(List<HourlyCallStatsDto> hourlyStats) {
        int sum = 0;
        for (HourlyCallStatsDto stat : hourlyStats) {
            sum += stat.getTotalCalls();
        }
        return sum;
    }

    private List<HourlyCallStatsDto> extractHourlyCallStatsFromResponse(SuccessResponse<?> successResponse) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> mappedData = (List<Map<String, Object>>) successResponse.getData();

        return mappedData.stream()
                .map(map -> mapper.convertValue(map, HourlyCallStatsDto.class)
                )
                .toList();
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
        assertThat(successResponse.getData()).isNotNull();
        List<HourlyCallStatsDto> hourlyStats = extractHourlyCallStatsFromResponse(successResponse);
        assertThat(hourlyStats).isNotEmpty();
        assertThat(hourlyStats.size()).isEqualTo(24);
        int sum = aggregateTotalCalls(hourlyStats);
        assertThat(sum).isGreaterThan(0);
    }

    @Test
    void whenValidRequestOptionalParams_thenReturns200WithData() throws URISyntaxException, JsonProcessingException {
        restTemplate = new TestRestTemplate().withBasicAuth("admin", "admin123");
        int startHour = 10;
        int endHour = 22;
        uri = new URI(baseUrl + ":" + port +
                "/api/heatmap/answer-rate?dateInput=2025-04-10&numberOfShades=7&startHour="+ startHour + "&endHour=" + endHour);
        ResponseEntity<SuccessResponse> responseEntity = restTemplate.getForEntity(uri, SuccessResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Object jsonObj = responseEntity.getBody();
        assertThat(jsonObj).isInstanceOf(SuccessResponse.class);
        String jsonStr = mapper.writeValueAsString(jsonObj);
        SuccessResponse<?> successResponse = mapper.readValue(jsonStr, SuccessResponse.class);
        assertThat(successResponse.getData()).isNotNull();
        List<HourlyCallStatsDto> hourlyStats = extractHourlyCallStatsFromResponse(successResponse);
        assertThat(hourlyStats).hasSize(endHour - startHour + 1);
        int sum = aggregateTotalCalls(hourlyStats);
        assertThat(sum).isGreaterThan(0);
    }

    @Test
    void whenStartHourTheSameAsEndHour_thenReturns200WithData() throws URISyntaxException, JsonProcessingException {
        restTemplate = new TestRestTemplate().withBasicAuth("admin", "admin123");
        int startHour = 12;
        int endHour = 12;
        int numberOfShades = 7;
        uri = new URI(baseUrl + ":" + port +
                "/api/heatmap/answer-rate?dateInput=2025-04-15&numberOfShades=" + numberOfShades + "&startHour=" + startHour + "&endHour=" + endHour);
        ResponseEntity<SuccessResponse> responseEntity = restTemplate.getForEntity(uri, SuccessResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Object jsonObj = responseEntity.getBody();
        assertThat(jsonObj).isInstanceOf(SuccessResponse.class);
        String jsonStr = mapper.writeValueAsString(jsonObj);
        SuccessResponse<?> successResponse = mapper.readValue(jsonStr, SuccessResponse.class);
        assertThat(successResponse.getData()).isNotNull();
        List<HourlyCallStatsDto> hourlyStats = extractHourlyCallStatsFromResponse(successResponse);
        assertThat(hourlyStats).hasSize(1);
        HourlyCallStatsDto stat = hourlyStats.get(0);
        assertThat(stat.getHour()).isEqualTo(startHour);
        assertThat(stat.getTotalCalls()).isGreaterThan(0);
        assertThat(stat.getRate()).isEqualTo(stat.getAnsweredCalls() / (float) stat.getTotalCalls() * 100);
        int shadeNumber = getShadeNumber(numberOfShades, stat);
        assertThat(stat.getShade()).isEqualTo(String.format("Shade%d", shadeNumber));
    }

    private static int getShadeNumber(int numberOfShades, HourlyCallStatsDto stat) {
        int shadeNumber = (int) (numberOfShades * stat.getRate() / 100) + 1;
        return Math.min(shadeNumber, numberOfShades); // if the rate is 100%, the shade number should be 7, not 8.
    }
}
