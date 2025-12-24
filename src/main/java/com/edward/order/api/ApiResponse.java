package com.edward.order.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private Pagination pagination;
}
