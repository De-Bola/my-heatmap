package com.onoff.heatmap.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HeatMap API")
                        .version("0.0.1")
                        .description("API documentation for generating answer rate info based on\n" +
                                "call logs of an organization, formatted for potential use in a heatmap visualization"));
    }
}