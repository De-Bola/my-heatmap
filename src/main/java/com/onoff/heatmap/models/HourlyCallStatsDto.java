package com.onoff.heatmap.models;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class HourlyCallStatsDto {

    private int hour;
    private int answeredCalls;
    private int totalCalls;
    private float rate;
    private String shade;

    public HourlyCallStatsDto(int hour, int answeredCalls, int totalCalls, int numberOfShades) {
        this.hour = hour;
        this.answeredCalls = answeredCalls;
        this.totalCalls = totalCalls;
        this.rate = totalCalls > 0 ? (answeredCalls * 100.0f / totalCalls) : 0.0f;
        this.shade = calculateShade(rate, numberOfShades);
    }

    private String calculateShade(float rate, int numberOfShades) {
        float step = 100.0f / numberOfShades;
        int shadeIndex = (int) Math.min((rate / step) + 1, numberOfShades);
        return "Shade" + shadeIndex;
    }
}
