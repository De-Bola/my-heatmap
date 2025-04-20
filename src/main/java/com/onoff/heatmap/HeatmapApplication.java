package com.onoff.heatmap;

import com.onoff.heatmap.config.RequestConstraintsProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RequestConstraintsProps.class)
public class HeatmapApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeatmapApplication.class, args);
	}
	//uri -> http://localhost:8080/swagger-ui.html
}
