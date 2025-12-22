package com.edward.order.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public final class JsonMapperUtils {

    private static ObjectMapper objectMapper;
    private static Validator validator;

    private JsonMapperUtils() {}

    public static void init(ObjectMapper mapper, Validator val) {
        if (JsonMapperUtils.objectMapper != null) {
            return; // prevent double init
        }
        JsonMapperUtils.objectMapper = mapper;
        JsonMapperUtils.validator = val;
    }

    public static <T> T mapper(String json, Class<T> clazz) {
        try {
            T obj = objectMapper.readValue(json, clazz);
            validate(obj);
            return obj;
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid JSON format", ex);
        }
    }

    public static <T> T mapper(String json, TypeReference<T> type) {
        try {
            T obj = objectMapper.readValue(json, type);
            validate(obj);
            return obj;
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid JSON format", ex);
        }
    }

    private static <T> void validate(T obj) {
        if (validator == null) return;

        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private static void ensureInitialized() {
        if (objectMapper == null || validator == null) {
            throw new IllegalStateException(
                    "JsonMapperUtils is not initialized. Check Spring config."
            );
        }
    }
}
