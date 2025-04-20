package com.onoff.heatmap.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "heatmap.constraints")
public class RequestConstraintsProps {
    private int numberOfShadesMin;
    private int numberOfShadesMax;
    private int hourMin;
    private int hourMax;
}
