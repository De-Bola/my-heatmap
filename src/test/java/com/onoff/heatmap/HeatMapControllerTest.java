package com.onoff.heatmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class HeatMapControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void GetHeatMap() throws Exception {
        this.mockMvc.perform(get("/api/heatmap").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to heatmap"));
    }

    @Test
    public void GetHeatMapWithUsername() throws Exception {
        this.mockMvc.perform(get("/api/heatmap/me").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Welcome to admin's heatmap"));
    }

    @Test
    public void GetHeatMapWithWrongPassword() throws Exception {
        this.mockMvc.perform(get("/api/heatmap/me").with(httpBasic("admin", "admin12")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void GetHeatMapWhenUnauthenticatedShouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(get("/api/heatmap/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void GetCallLogsShouldReturnCallLogs() throws Exception {
        this.mockMvc.perform(get("/api/heatmap/calls/logs/all").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.page").doesNotExist());
    }

    @Test
    public void GetCallLogsShouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(get("/api/heatmap/calls/logs/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void GetPageableCallLogsShouldReturnFirstPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/heatmap/calls/logs/pages?page=0&size=5&sort=duration,asc")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.data[0].duration").exists())
                .andExpect(jsonPath("$.data[4].duration").exists())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        int firstDuration = root.path("data").get(0).path("duration").asInt();
        int lastDuration = root.path("data").get(4).path("duration").asInt();

        assertThat(lastDuration).isGreaterThan(firstDuration);
    }

    @Test
    public void GetPageableCallLogsByDateShouldReturnEmpty() throws Exception {
        mockMvc.perform(
                get("/api/heatmap/calls/logs?dateInput=1900-01-01")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.data").isEmpty())
                .andReturn();
    }

    @Test
    public void GetPageableCallLogsByDateShouldReturnFirstPage() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/heatmap/calls/logs?dateInput=2024-04-10")
                                .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.page").exists())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        System.out.println(json);
    }

    @Test
    void whenInvalidNumberOfShades_thenReturns400() throws Exception {
        mockMvc.perform(get("/api/heatmap/answer-rate")
                        .param("dateInput", "2024-04-10")
                        .param("numberOfShades", "2")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Minimum numberOfShades is 3")));
    }

}
