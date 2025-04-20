package com.onoff.heatmap.controllers.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class SuccessResponse<T> {
    private T data;
    private String message;

    public SuccessResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

}
