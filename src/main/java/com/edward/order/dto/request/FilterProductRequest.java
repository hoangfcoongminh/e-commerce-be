package com.edward.order.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class FilterProductRequest {

    private List<Long> subCategoryIds;
    private String keyword;
    private Long minPrice;
    private Long maxPrice;
}
