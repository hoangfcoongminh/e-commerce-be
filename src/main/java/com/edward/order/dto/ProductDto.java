package com.edward.order.dto;

import com.edward.order.entity.Product;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private Long id;
    private Long subCategoryId;
    private String name;
    private String description;
    private Long originalPrice;
    private Long realPrice;
    private Integer stock;
    private String slug;
    private Integer status;
    private PromotionDto promotion;

    public static Product of(ProductDto dto) {
        return Product.builder()
                .subCategoryId(dto.getSubCategoryId())
                .name(dto.getName())
                .description(dto.getDescription())
                .originalPrice(dto.getOriginalPrice())
                .stock(dto.getStock())
                .build();
    }

    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .subCategoryId(product.getSubCategoryId())
                .name(product.getName())
                .description(product.getDescription())
                .originalPrice(product.getOriginalPrice())
                .stock(product.getStock())
                .slug(product.getSlug())
                .status(product.getStatus())
                .build();
    }
}
