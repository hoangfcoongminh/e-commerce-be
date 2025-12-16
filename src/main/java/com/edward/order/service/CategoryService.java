package com.edward.order.service;

import com.edward.order.dto.CategoryDto;
import com.edward.order.entity.Category;
import com.edward.order.entity.Product;
import com.edward.order.entity.SubCategory;
import com.edward.order.enums.EntityStatus;
import com.edward.order.exception.BusinessException;
import com.edward.order.repository.CategoryRepository;
import com.edward.order.repository.ProductRepository;
import com.edward.order.repository.SubCategoryRepository;
import com.edward.order.utils.SlugUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;

    public List<CategoryDto> getAll() {
        return categoryRepository.findAllAndActive()
                .stream()
                .map(CategoryDto::toDto)
                .toList();
    }

    public Page<CategoryDto> search(String search, Integer status, Pageable pageable) {
        search = (search == null || search.isBlank()) ? null : search.toLowerCase();
        Page<Category> data = categoryRepository.search(search, status, pageable);
        List<CategoryDto> response = data.getContent()
                .stream()
                .map(CategoryDto::toDto)
                .toList();
        return new PageImpl<>(response, pageable, data.getTotalElements());
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        String slug = SlugUtils.toSlug(dto.getName());
        if (categoryRepository.existsBySlugAndActive(slug)) {
            throw new BusinessException("category.invalid");
        }
        Category category = CategoryDto.of(dto);
        category.setId(null);
        category.setSlug(slug);
        category = categoryRepository.save(category);

        return CategoryDto.toDto(category);
    }

    @Transactional
    public CategoryDto update(CategoryDto dto) {
        Category c = validate(dto);
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setSlug(SlugUtils.toSlug(dto.getName()));
        c.setStatus(dto.getStatus() != null ? c.getStatus() : EntityStatus.ACTIVE.getValue());
        c = categoryRepository.save(c);

        return CategoryDto.toDto(c);
    }

    private Category validate(CategoryDto dto) {
        if (dto.getId() == null) {
            throw new BusinessException("category.invalid");
        }
        Category c = categoryRepository.findByIdAndActive(dto.getId()).orElseThrow(() -> new BusinessException("category.not.found"));
        String slug = SlugUtils.toSlug(dto.getName());
        if (!slug.equals(c.getSlug()) && categoryRepository.existsBySlugAndActive(slug)) {
            throw new BusinessException("category.name.invalid");
        }
        return c;
    }

    @Transactional
    public String delete(Long id) {
        if (categoryRepository.findByIdAndActive(id).isEmpty()) {
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
