package com.edward.order.service;

import com.edward.order.dto.SubCategoryDto;
import com.edward.order.dto.request.SearchSubCategoryRq;
import com.edward.order.entity.SubCategory;
import com.edward.order.entity.Product;
import com.edward.order.enums.EntityStatus;
import com.edward.order.exception.BusinessException;
import com.edward.order.repository.CategoryRepository;
import com.edward.order.repository.ProductRepository;
import com.edward.order.repository.SubCategoryRepository;
import com.edward.order.utils.SlugUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;

    public List<SubCategoryDto> getAll() {
        return subCategoryRepository.findAll()
                .stream()
                .map(SubCategoryDto::toDto)
                .toList();
    }

    public List<SubCategoryDto> getAllAndActive() {
        return subCategoryRepository.findAllAndActive()
                .stream()
                .map(SubCategoryDto::toDto)
                .toList();
    }

    public Page<SubCategoryDto> search(SearchSubCategoryRq request, Pageable pageable) {
        String search = (request.getSearch() == null || request.getSearch().isBlank()) ? null : request.getSearch().toLowerCase();
        Page<SubCategory> data = subCategoryRepository.search(search, request.getCategoryIds(), request.getStatus(), pageable);
        List<SubCategoryDto> response = data.getContent()
                .stream()
                .map(SubCategoryDto::toDto)
                .toList();
        return new PageImpl<>(response, pageable, data.getTotalElements());
    }

    @Transactional
    public SubCategoryDto create(SubCategoryDto dto) {
        if (categoryRepository.findByIdAndActive(dto.getCategoryId()).isEmpty()) {
            throw new BusinessException("category.not.found");
        }
        String slug = SlugUtils.generateUniqueSlug(dto.getName(), subCategoryRepository);
        if (subCategoryRepository.findBySlugAndActive(slug).isPresent()) {
            throw new BusinessException("sub.category.invalid");
        }
        SubCategory subCategory = SubCategoryDto.of(dto);
        subCategory.setId(null);
        subCategory.setSlug(slug);
        subCategory.setStatus(dto.getStatus() != null ? dto.getStatus() : EntityStatus.ACTIVE.getValue());
        subCategory = subCategoryRepository.save(subCategory);

        return SubCategoryDto.toDto(subCategory);
    }

    @Transactional
    public SubCategoryDto update(SubCategoryDto dto) {
        SubCategory s = validate(dto);
        s.setCategoryId(dto.getCategoryId());
        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        s.setSlug(SlugUtils.generateUniqueSlug(dto.getName(), subCategoryRepository));
        s.setStatus(dto.getStatus() != null ? s.getStatus() : EntityStatus.ACTIVE.getValue());
        s = subCategoryRepository.save(s);

        return SubCategoryDto.toDto(s);
    }

    private SubCategory validate(SubCategoryDto dto) {
        if (dto.getId() == null) {
            throw new BusinessException("sub.category.invalid");
        }
        if (categoryRepository.findByIdAndActive(dto.getCategoryId()).isEmpty()) {
            throw new BusinessException("category.not.found");
        }
        SubCategory c = subCategoryRepository.findByIdAndActive(dto.getId()).orElseThrow(() -> new BusinessException("sub.category.not.found"));
        String slug = SlugUtils.generateUniqueSlug(dto.getName(), subCategoryRepository);
        if (!slug.equals(c.getSlug()) && subCategoryRepository.findBySlugAndActive(slug).isPresent()) {
            throw new BusinessException("category.name.invalid");
        }
        return c;
    }

    @Transactional
    public String delete(Long id) {
        if (subCategoryRepository.findByIdAndActive(id).isEmpty()) {
            throw new BusinessException("sub.category.not.found");
        }
        subCategoryRepository.deleteById(id);

        List<Product> products = productRepository.findAllBySubCategoryIdIn(List.of(id));
        productRepository.deleteAll(products);

        return "success";
    }
}
