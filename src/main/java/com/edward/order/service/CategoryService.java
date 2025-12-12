package com.edward.order.service;

import com.edward.order.dto.CategoryDto;
import com.edward.order.entity.Category;
import com.edward.order.exception.BusinessException;
import com.edward.order.repository.CategoryRepository;
import com.edward.order.utils.SlugUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryDto::toDto);
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        String slug = SlugUtils.toSlug(dto.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException("slug.already.exists");
        }
        Category category = CategoryDto.of(dto);
        category.setSlug(slug);
        category = categoryRepository.save(category);

        return CategoryDto.toDto(category);
    }
}
