package com.edward.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityStatus {

    ACTIVE(1),
    INACTIVE(0);

    private final Integer value;
}
