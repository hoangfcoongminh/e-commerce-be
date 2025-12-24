package com.edward.order.config;

import com.edward.order.utils.JsonMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import jakarta.validation.Validator;

@Component
public class JsonMapperInitializer {

    public JsonMapperInitializer(ObjectMapper objectMapper, Validator validator) {
        JsonMapperUtils.init(objectMapper, validator);
    }
}
