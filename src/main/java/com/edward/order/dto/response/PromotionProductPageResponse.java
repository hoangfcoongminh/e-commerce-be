package com.edward.order.dto.response;

import com.edward.order.api.PageResponse;
import com.edward.order.dto.ProductDto;
import com.edward.order.dto.PromotionDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PromotionProductPageResponse {
    private PromotionDto promotion;
    private PageResponse<ProductDto> productPage;
}
