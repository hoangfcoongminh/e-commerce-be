package com.edward.order.service.admin;

import com.edward.order.api.PageResponse;
import com.edward.order.dto.ProductDto;
import com.edward.order.dto.PromotionDto;
import com.edward.order.dto.request.CreatePromotionRequest;
import com.edward.order.dto.response.PromotionProductPageResponse;
import com.edward.order.entity.Product;
import com.edward.order.entity.Promotion;
import com.edward.order.entity.PromotionProduct;
import com.edward.order.exception.BusinessException;
import com.edward.order.repository.PromotionProductRepository;
import com.edward.order.repository.PromotionRepository;
import com.edward.order.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;

    public PromotionProductPageResponse getProductsInPromotion(String slug, Pageable pageable) {
        Promotion promotion = promotionRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException("promotion.not.found"));
        Page<Product> products = promotionProductRepository.findAllByPromotionSlug(slug, pageable);

        return PromotionProductPageResponse.builder()
                .promotion(PromotionDto.toDto(promotion))
                .productPage(PageResponse.of(products.map(ProductDto::toDto)))
                .build();
    }

    public PromotionDto createPromotion(CreatePromotionRequest request) {
        String slug = SlugUtils.generateUniqueSlug(request.getName(), promotionRepository);

        Promotion promotion = CreatePromotionRequest.of(request);
        promotion.setSlug(slug);

        promotion = promotionRepository.save(promotion);

        Promotion finalPromotion = promotion;
        List<PromotionProduct> promotionProducts = request.getProductIds().stream()
                .map(productId -> PromotionProduct.builder()
                        .promotionId(finalPromotion.getId())
                        .productId(productId)
                        .build())
                .toList();
        promotionProductRepository.saveAll(promotionProducts);

        return PromotionDto.toDto(promotion);
    }
}
