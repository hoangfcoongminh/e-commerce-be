package com.edward.order.utils;

import com.edward.order.api.ApiResponse;
import com.edward.order.api.PageResponse;
import com.edward.order.api.Pagination;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

public final class ResponseUtils {

    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    public static String getCurrentUrl() {
        HttpServletRequest req = getCurrentRequest();
        return req != null ? req.getRequestURI() : null;
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {

        ApiResponse<T> response = ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .url(getCurrentUrl())
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<PageResponse<T>>> successPage(Page<T> page) {

        ApiResponse<PageResponse<T>> response = ApiResponse.<PageResponse<T>>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .url(getCurrentUrl())
                .data(PageResponse.of(page))
                .build();

        return ResponseEntity.ok(response);
    }
}
