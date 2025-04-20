package com.onoff.heatmap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onoff.heatmap.config.RequestConstraintsProps;
import com.onoff.heatmap.config.TestConfig;
import com.onoff.heatmap.controllers.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class HeatMapControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    private RequestConstraintsProps props;

    private int numberOfShadesMin;
    private int numberOfShadesMax;
    private int hourMin;
    private int hourMax;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        this.numberOfShadesMin = props.getNumberOfShadesMin();
        this.numberOfShadesMax = props.getNumberOfShadesMax();
        this.hourMin = props.getHourMin();
        this.hourMax = props.getHourMax();
    }

    @Test
    void whenInvalidNumberOfShades_thenReturns400() throws Exception {
        mockMvc.perform(get("/api/heatmap/answer-rate")
                        .param("dateInput", "2024-04-10")
                        .param("numberOfShades", "2")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        containsString(String.format("must be between %d and %d", numberOfShadesMin, numberOfShadesMax))));
    }

    @Test
    void whenNumberOfShadesGreaterThan10_thenReturns400() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/heatmap/answer-rate")
                        .param("dateInput", "2024-04-10")
                        .param("numberOfShades", "13")
                        .with(httpBasic("admin", "admin123")))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Validation failed");
        assertThat(Integer.parseInt(errorResponse.getCode())).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getFieldErrors()).containsEntry("numberOfShades", String.format("must be between %d and %d", numberOfShadesMin, numberOfShadesMax));
    }

    @Test
    void whenInvalidStartHour_thenReturns400() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/heatmap/answer-rate")
                        .param("dateInput", "2024-04-10")
                        .param("numberOfShades", "7")
                        .param("startHour", "25")
                        .with(httpBasic("admin", "admin123")))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Validation failed");
        assertThat(Integer.parseInt(errorResponse.getCode())).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getFieldErrors()).containsEntry("startHour", String.format("must be between %d and %d", hourMin, hourMax));
    }

    @Test
    void whenMultipleConstraintViolations_thenReturns400() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/heatmap/answer-rate")
                        .param("dateInput", "2024-04-10")
                        .param("numberOfShades", "1")
                        .param("startHour", "25")
                        .param("endHour", "30")
                        .with(httpBasic("admin", "admin123")))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Validation failed");
        assertThat(Integer.parseInt(errorResponse.getCode())).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getFieldErrors()).size().isEqualTo(3);
    }

    @Test
    void whenInvalidStartHourAndEndHour_thenReturns400() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/heatmap/answer-rate")
                        .param("dateInput", "2024-04-10")
                        .param("numberOfShades", "7")
                        .param("startHour", "20")
                        .param("endHour", "10")
                        .with(httpBasic("admin", "admin123")))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("endHour must be greater than or equal to startHour");
        assertThat(Integer.parseInt(errorResponse.getCode())).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
