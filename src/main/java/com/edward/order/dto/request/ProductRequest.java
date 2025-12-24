package com.edward.order.dto.request;

import com.edward.order.entity.Product;
import com.edward.order.enums.EntityStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductRequest {

    private Long id;

    @NotNull(message = "Sub-category ID cannot be null")
    private Long subCategoryId;

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Original price cannot be null")
    @Min(value = 0, message = "Original price must be non-negative")
    private Long originalPrice;

    @NotNull(message = "Stock cannot be null")
    @Min(value = 0, message = "Stock must be non-negative")
    private Integer stock;

    @NotBlank(message = "Slug cannot be blank")
    private String slug;

    private Integer status;

    private List<Long> promotionIds;

    private List<String> imageNames;

    public static Product of(ProductRequest request) {
        return Product.builder()
                .id(request.getId())
                .subCategoryId(request.getSubCategoryId())
                .name(request.getName())
                .description(request.getDescription())
                .originalPrice(request.getOriginalPrice())
                .stock(request.getStock())
                .slug(request.getSlug())
                .status(request.getStatus() == null ? EntityStatus.ACTIVE.getValue() : request.getStatus())
                .build();
    }
}
