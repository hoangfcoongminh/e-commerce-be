package com.edward.order.utils;

import com.edward.order.api.ApiResponse;
import com.edward.order.api.Pagination;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

public class ResponseUtils {

    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    public static String getCurrentUrl() {
        HttpServletRequest req = getCurrentRequest();
        return req != null ? req.getRequestURI() : null;
    }

    public static ResponseEntity<ApiResponse<Object>> success(Object data) {

        ApiResponse<Object> response = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .url(getCurrentUrl())
                .build();

        if (data instanceof Page<?> page) {
            response.setData(page.getContent());
            Pagination paging = Pagination.builder()
                    .page(page.getNumber())
                    .size(page.getSize())
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .build();
            response.setPagination(paging);
        } else {
            response.setData(data);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
