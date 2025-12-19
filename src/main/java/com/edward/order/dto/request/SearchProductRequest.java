package com.edward.order.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class SearchProductRequest {

    List<Long> categoryIds;
    List<Long> subCategoryIds;
    String search;
    Long minPrice;
    Long maxPrice;
}
