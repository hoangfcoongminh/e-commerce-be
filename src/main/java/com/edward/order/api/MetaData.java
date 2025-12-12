package com.edward.order.api;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MetaData {

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private String sort;
}
