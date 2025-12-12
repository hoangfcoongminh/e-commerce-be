package com.edward.order.utils;

import com.edward.order.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

public class ResponseUtils {

    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private static String getCurrentUrl() {
        HttpServletRequest req = getCurrentRequest();
        return req != null ? req.getRequestURI() : null;
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {

        HttpServletRequest req = getCurrentRequest();

        ApiResponse<T> response = ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .url(getCurrentUrl())
                .data(data)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
