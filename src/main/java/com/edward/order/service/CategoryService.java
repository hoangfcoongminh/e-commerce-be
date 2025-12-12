package com.edward.order.service;

import com.edward.order.dto.CategoryDto;
import com.edward.order.entity.Category;
import com.edward.order.entity.Product;
import com.edward.order.entity.SubCategory;
import com.edward.order.exception.BusinessException;
import com.edward.order.repository.CategoryRepository;
import com.edward.order.repository.ProductRepository;
import com.edward.order.repository.SubCategoryRepository;
import com.edward.order.utils.SlugUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;

    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryDto::toDto);
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        String slug = SlugUtils.toSlug(dto.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException("category.invalid");
        }
        Category category = CategoryDto.of(dto);
        category.setSlug(slug);
        category = categoryRepository.save(category);

        return CategoryDto.toDto(category);
    }

    @Transactional
    public CategoryDto update(CategoryDto dto) {
        Category c = validate(dto);
        c = CategoryDto.of(dto);
        c.setSlug(SlugUtils.toSlug(dto.getName()));
        c = categoryRepository.save(c);

        return CategoryDto.toDto(c);
    }

    private Category validate(CategoryDto dto) {
        if (dto.getId() == null) {
            throw new BusinessException("category.invalid");
        }
        Category c = categoryRepository.findById(dto.getId()).orElseThrow(() -> new BusinessException("category.not.found"));
        String slug = SlugUtils.toSlug(dto.getName());
        if (!slug.equals(c.getSlug()) && categoryRepository.existsBySlug(slug)) {
            throw new BusinessException("category.name.invalid");
        }
        return c;
    }

    @Transactional
    public String delete(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new BusinessException("category.not.found");
        }
        categoryRepository.deleteById(id);

        List<Long> subCategoryIds = subCategoryRepository.findAllByCategoryId(id)
                .stream().map(SubCategory::getId).toList();
        subCategoryRepository.deleteAllById(subCategoryIds);


        List<Product> products = productRepository.findAllBySubCategoryIdIn(subCategoryIds);
        productRepository.deleteAll(products);

        return "success";
    }
}
