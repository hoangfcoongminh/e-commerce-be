package com.edward.order.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchSubCategoryRq {

    private String search;
    private List<Long> categoryIds;
    private Integer status;
}
