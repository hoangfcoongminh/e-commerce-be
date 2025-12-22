package com.edward.order.service;

import com.edward.order.dto.ProductDto;
import com.edward.order.dto.PromotionDto;
import com.edward.order.dto.request.CreateProductRequest;
import com.edward.order.dto.request.SearchProductRequest;
import com.edward.order.entity.Product;
import com.edward.order.entity.ProductImage;
import com.edward.order.entity.Promotion;
import com.edward.order.entity.PromotionProduct;
import com.edward.order.enums.EntityStatus;
import com.edward.order.exception.BusinessException;
import com.edward.order.repository.*;
import com.edward.order.utils.JsonMapperUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final SubCategoryRepository subCategoryRepository;
    private final R2StorageService r2StorageService;
    private final ProductImageRepository productImageRepository;

    private static final String PRODUCT_IMAGE_FOLDER = "products";

    public Page<ProductDto> getAll(Pageable pageable) {
        Page<Product> data = productRepository.findAllAndActive(pageable);
        List<ProductDto> response = toResponse(data.getContent());
        return new PageImpl<>(response, pageable, data.getTotalElements());
    }

    public Page<ProductDto> search(SearchProductRequest request, Pageable pageable) {
        String search = (request.getSearch() == null || request.getSearch().isBlank()) ? null : request.getSearch().toLowerCase();

        Page<Product> data = productRepository.search(search, request.getSubCategoryIds(), pageable);
        List<Product> products = data.getContent();

        List<ProductDto> response = toResponse(products);
        response = response.stream()
                .filter(p -> p.getRealPrice() >= request.getMinPrice() && p.getRealPrice() <= request.getMaxPrice())
                .toList();

        return new PageImpl<>(response, pageable, data.getTotalElements());
    }

    private List<ProductDto> toResponse(List<Product> products) {
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();
        List<PromotionProduct> promotionProducts = promotionProductRepository.findAllByProductIds(productIds);

        Map<Long, List<Promotion>> promotionMap = null;
        if (!promotionProducts.isEmpty()) {
            promotionMap = getPromotionMap(promotionProducts);
        }
//        Map<Long, >

        List<ProductDto> response = new ArrayList<>();
        for (Product product : products) {
            ProductDto dto = ProductDto.toDto(product);

            if (promotionMap == null) {
                dto.setRealPrice(product.getOriginalPrice());
                dto.setPromotions(null);
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
                var dtos = promotions.stream().map(PromotionDto::toDto).toList();
                dtos.forEach(d -> {
                    d.setBestDeal(d.getId().equals(bestPromotion.get().getId()));
                });
                dto.setPromotions(dtos);

            }
            dto.setRealPrice(realPrice.get());
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

    public List<ProductDto> createProducts(String jsonRequests, List<MultipartFile> files) {
        // Parse JSON requests
        List<CreateProductRequest> requests =
                JsonMapperUtils.mapper(
                        jsonRequests,
                        new TypeReference<List<CreateProductRequest>>() {}
                );

        // Validate requests
        validateCreateProducts(requests);

        // Save products
        List<Product> products = new ArrayList<>();
        for (CreateProductRequest request : requests) {
            Product p = CreateProductRequest.of(request);
            p.setId(null);
            p.setStatus(EntityStatus.ACTIVE.getValue());
            products.add(p);
        }
        products = productRepository.saveAll(products);

        // Save new product to promotions
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            List<Long> promotionIds = requests.get(i).getPromotionIds();

            if (promotionIds != null && !promotionIds.isEmpty()) {
                List<PromotionProduct> promotionProducts = new ArrayList<>();
                for (Long promotionId : promotionIds) {
                    PromotionProduct pp = new PromotionProduct(null, promotionId, p.getId());
                    promotionProducts.add(pp);
                }
                promotionProductRepository.saveAll(promotionProducts);
            }
        }

        // Handle images
        Map<String, MultipartFile> imagesMap = files.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, f -> f));

        List<ProductImage> productImages = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            List<String> imageNamesOfProduct = requests.get(i).getImageNames();
            List<MultipartFile> imagesToUpload = new ArrayList<>();
            if (imageNamesOfProduct != null) {
                for (String imageName : imageNamesOfProduct) {
                    if (imagesMap.containsKey(imageName)) {
                        imagesToUpload.add(imagesMap.get(imageName));
                    }
                }
                Map<String, String> imageUrlMap = r2StorageService.bulkUpload(imagesToUpload, PRODUCT_IMAGE_FOLDER + "/" + products.get(i).getId());
                for (String imageName : imageNamesOfProduct) {
                    ProductImage productImage = new ProductImage();
                    productImage.setProductId(products.get(i).getId());
                    productImage.setUrl(imageUrlMap.get(imageName));
                    productImages.add(productImage);
                }
            }
        }
        productImageRepository.saveAll(productImages);
        return toResponse(products);
    }

    private void validateCreateProducts(List<CreateProductRequest> requests) {
        if (requests.isEmpty()) {
            throw new BusinessException("Request list cannot be empty.");
        }
        List<Long> subCategoryIds = requests.stream()
                .map(CreateProductRequest::getSubCategoryId)
                .distinct()
                .toList();
        if (subCategoryIds.size() != subCategoryRepository.countActiveByCategoryIds(subCategoryIds)) {
            throw new BusinessException("One or more sub-categories do not exist or are inactive.");
        }

        List<String> slugs = requests.stream()
                .map(CreateProductRequest::getSlug)
                .toList();
        if (slugs.size() != requests.size() || slugs.size() != productRepository.countActiveBySlugs(slugs)) {
            throw new BusinessException("One or more slugs are duplicate or already exist.");
        }
        List<Long> promotionIds = requests.stream()
                .flatMap(r -> r.getPromotionIds() != null ? r.getPromotionIds().stream() : null)
                .distinct()
                .toList();
        if (promotionIds.size() != promotionRepository.countActiveByIds(promotionIds)) {
            throw new BusinessException("One or more promotions do not exist or are inactive.");
        }
    }
}
