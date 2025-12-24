package com.edward.order.dto;

import com.edward.order.entity.SubCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubCategoryDto {

    private Long id;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Sub-category name is required")
    private String name;

    private String description;

    private String slug;

    private Integer status;

    public static SubCategory of(SubCategoryDto dto) {
        return SubCategory.builder()
                .categoryId(dto.getCategoryId())
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .build();
    }

    public static SubCategoryDto toDto(SubCategory subCategory) {
        return SubCategoryDto.builder()
                .id(subCategory.getId())
                .categoryId(subCategory.getCategoryId())
                .name(subCategory.getName())
                .description(subCategory.getDescription())
                .slug(subCategory.getSlug())
                .status(subCategory.getStatus())
                .build();
    }
}
