package com.edward.order.service;

import com.edward.order.dto.ProductDto;
import com.edward.order.dto.PromotionDto;
import com.edward.order.dto.request.SearchProductRequest;
import com.edward.order.entity.Product;
import com.edward.order.entity.Promotion;
import com.edward.order.entity.PromotionProduct;
import com.edward.order.repository.ProductRepository;
import com.edward.order.repository.PromotionProductRepository;
import com.edward.order.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionRepository promotionRepository;

    public Page<ProductDto> getAll(Pageable pageable) {
        Page<Product> data = productRepository.findAllAndActive(pageable);
        List<ProductDto> response = toResponseWithPromotion(data.getContent());
        return new PageImpl<>(response, pageable, data.getTotalElements());
    }

    public Page<ProductDto> search(SearchProductRequest request, Pageable pageable) {
        String search = (request.getSearch() == null || request.getSearch().isBlank()) ? null : request.getSearch().toLowerCase();

        Page<Product> data = productRepository.search(search, request.getSubCategoryIds(), pageable);
        List<Product> products = data.getContent();

        List<ProductDto> response = toResponseWithPromotion(products);
        response = response.stream()
                .filter(p -> p.getRealPrice() >= request.getMinPrice() && p.getRealPrice() <= request.getMaxPrice())
                .toList();

        return new PageImpl<>(response, pageable, data.getTotalElements());
    }

    private List<ProductDto> toResponseWithPromotion(List<Product> products) {
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();
        List<PromotionProduct> promotionProducts = promotionProductRepository.findAllByProductIds(productIds);

        Map<Long, List<Promotion>> promotionMap = null;
        if (!promotionProducts.isEmpty()) {
            promotionMap = getPromotionMap(promotionProducts);
        }

        List<ProductDto> response = new ArrayList<>();
        for (Product product : products) {
            if (promotionMap == null) {
                ProductDto dto = ProductDto.toDto(product);
                dto.setRealPrice(product.getOriginalPrice());
                dto.setPromotion(null);
                response.add(dto);
                continue;
            }
            List<Promotion> promotions = promotionMap.getOrDefault(product.getId(), null);
            AtomicReference<Long> realPrice = new AtomicReference<>(product.getOriginalPrice());
            AtomicReference<Long> minPrice = new AtomicReference<>(product.getOriginalPrice());
            AtomicReference<Promotion> bestPromotion = new AtomicReference<>();

            if (promotions != null) {
                promotions.forEach(promotion -> {
                    Long discount = (product.getOriginalPrice() * promotion.getDiscountPercent()) / 100;
                    if (discount > promotion.getDiscountAmount()) {
                        realPrice.updateAndGet(v -> v - promotion.getDiscountAmount());
                    } else {
                        realPrice.updateAndGet(v -> v - discount);
                    }
                    if (realPrice.get() < minPrice.get()) {
                        minPrice.set(realPrice.get());
                        bestPromotion.set(promotion);
                    }
                });
            }
            ProductDto dto = ProductDto.toDto(product);
            dto.setRealPrice(realPrice.get());
            dto.setPromotion(PromotionDto.toDto(bestPromotion.get()));
            response.add(dto);
        }
        return response;
    }

    private Map<Long, List<Promotion>> getPromotionMap(List<PromotionProduct> promotionProducts) {
        List<Long> promotionIds = promotionProducts.stream()
                .map(PromotionProduct::getPromotionId)
                .distinct()
                .toList();
        Map<Long, Promotion> promotionMapById = promotionRepository.findAllByIdIn(promotionIds)
                .stream()
                .collect(Collectors.toMap(Promotion::getId, p -> p));
        return promotionProducts
                .stream()
                .collect(Collectors.toMap(PromotionProduct::getProductId, pp -> List.of(promotionMapById.get(pp.getPromotionId())),
                        (oldList, newList) -> {
                            oldList.addAll(newList);
                            return oldList;
                        }
                ));
    }
}
