package com.onoff.heatmap.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@EnableConfigurationProperties(RequestConstraintsProps.class) // 👈
public class TestConfig {
}
