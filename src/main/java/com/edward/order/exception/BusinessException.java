package com.edward.order.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}
