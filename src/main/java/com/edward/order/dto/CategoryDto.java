package com.edward.order.dto;

import com.edward.order.entity.Category;
import com.edward.order.enums.EntityStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;
    private String description;
    private String slug;
    private Integer status;

    private List<SubCategoryDto> subCategories;

    public static Category of(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : EntityStatus.ACTIVE.getValue())
                .build();
    }

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .status(category.getStatus())
                .build();
    }
}
