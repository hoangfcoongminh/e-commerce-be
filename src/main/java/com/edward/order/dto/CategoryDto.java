package com.edward.order.dto;

import com.edward.order.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;
    private String description;
    private String slug;

    public static Category of(CategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .build();
    }
}
