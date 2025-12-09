package com.edward.order.api;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private LocalDateTime timestamp;
    private boolean success;
    private String message;
    private String errorCode;
    private T data;
    private Map<String, String> errors;
    private String path;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("OK")
                .data(data)
                .build();
    }

    public static ApiResponse<?> error(String message, String errorCode) {
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    public ApiResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }

    public ApiResponse<T> withErrors(Map<String, String> errors) {
        this.errors = errors;
        return this;
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (Exception e) {
            return "{\"success\":false,\"code\":500,\"message\":\"serialization error\"}";
        }
    }
}
