package com.onoff.heatmap.controllers.response;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private Instant timestamp;
    private Map<String, String> fieldErrors;

    public ErrorResponse(String code, String message, Instant timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }
}

