package com.edward.order.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private LocalDateTime timestamp;
    private boolean success;
    private int status;
    private String errorCode;
    private String message;
    private String url;
    private T data;
    private Map<String, String> errors;

    private MetaData pagination;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("OK")
                .data(data)
                .build();
    }

//    public static ApiResponse<?> error(String message, String errorCode) {
//        return ApiResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .success(false)
//                .message(message)
//                .errorCode(errorCode)
//                .build();
//    }

    public ApiResponse<T> withUrl(String url) {
        this.url = url;
        return this;
    }

    public ApiResponse<T> withErrors(Map<String, String> errors) {
        this.errors = errors;
        return this;
    }
}
