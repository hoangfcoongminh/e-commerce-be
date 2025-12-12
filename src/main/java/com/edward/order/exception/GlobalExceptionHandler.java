package com.edward.order.exception;

import com.edward.order.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(
            BusinessException e,
            Locale locale
    ) {
        String message = messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale);

        ApiResponse<?> response = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(e.getErrorCode())
                .message(message)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest req,
            Locale locale
    ) {
        Map<String, String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> messageSource.getMessage(fe, locale),
                        (a, b) -> a));

        String message = messageSource.getMessage("validation.failed", null, "Validation failed", locale);

        ApiResponse<?> response = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(HttpStatus.BAD_REQUEST.name())
                .message(message)
                .url(req.getRequestURI())
                .errors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        Map<String, String> errorMap = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            field = field.substring(field.lastIndexOf(".") + 1); // lấy field name
            errorMap.put(field, violation.getMessage());
        });

        String message = messageSource.getMessage("invalid.parameters", null, "Invalid parameters", request.getLocale());

        ApiResponse<?> response = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("CONSTRAINT_VIOLATION")
                .message(message)
                .url(request.getRequestURI())
                .errors(errorMap)
                .build();

        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        String message = messageSource.getMessage("invalid.json", null, "Invalid JSON", request.getLocale());

        ApiResponse<?> response = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("INVALID_JSON")
                .message(message)
                .url(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(
            RuntimeException ex,
            WebRequest request
    ) {
        log.error("Unexpected RuntimeException", ex);

        ApiResponse<?> response = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .url(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(
            Exception ex,
            WebRequest request
    ) {
        log.error("Unhandled Exception", ex);

        ApiResponse<?> response = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .url(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
