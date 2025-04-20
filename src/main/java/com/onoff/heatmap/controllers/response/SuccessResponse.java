package com.onoff.heatmap.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@ToString
public class SuccessResponse<T> {
    private T data;
    private String message;
    private PageMetadata page;

    public SuccessResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public SuccessResponse(Page<?> pageData, String message) {
        this.data = (T) pageData.getContent();
        this.message = message;
        this.page = new PageMetadata(
                pageData.getSize(),
                pageData.getNumber(),
                pageData.getTotalPages(),
                pageData.getTotalElements()
        );
    }

    @Getter
    @AllArgsConstructor
    public static class PageMetadata {
        private int size;
        private int number;
        private int totalPages;
        private long totalElements;
    }
}
