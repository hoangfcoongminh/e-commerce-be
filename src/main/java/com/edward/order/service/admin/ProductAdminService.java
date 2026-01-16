package com.edward.order.service.admin;

import com.edward.order.dto.ProductDto;
import com.edward.order.dto.PromotionDto;
import com.edward.order.dto.request.ProductRequest;
import com.edward.order.dto.request.SearchProductRequest;
import com.edward.order.entity.Product;
import com.edward.order.entity.ProductImage;
import com.edward.order.entity.Promotion;
import com.edward.order.entity.PromotionProduct;
import com.edward.order.enums.EntityStatus;
import com.edward.order.exception.BusinessException;
import com.edward.order.repository.*;
import com.edward.order.service.R2StorageService;
import com.edward.order.utils.JsonMapperUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
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
public class ProductAdminService {

    private final ProductRepository productRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionRepository promotionRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final R2StorageService r2StorageService;
    private final ProductImageRepository productImageRepository;

    public Page<ProductDto> getAll(Pageable pageable) {
        Page<Product> data = productRepository.findAllAndActive(pageable);
        List<ProductDto> response = toResponse(data.getContent());
        return new PageImpl<>(response, pageable, data.getTotalElements());
    }

    public ProductDto getById(Long id) {
        Product product = productRepository.findByIdAndActive(id)
                .orElseThrow(() -> new BusinessException("Product not found"));
        List<ProductDto> response = toResponse(List.of(product));
        return response.get(0);
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

    public List<ProductDto> toResponse(List<Product> products) {
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();
        List<PromotionProduct> promotionProducts = promotionProductRepository.findAllByProductIdIn(productIds);

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

    @Transactional
    public List<ProductDto> createProducts(String jsonRequests, List<MultipartFile> files) {
        // Parse JSON requests
        List<ProductRequest> requests =
                JsonMapperUtils.mapper(
                        jsonRequests,
                        new TypeReference<List<ProductRequest>>() {
                        }
                );

        // Validate requests
        validateProduct(requests);

        // Save products
        List<Product> products = new ArrayList<>();
        for (ProductRequest request : requests) {
            Product p = ProductRequest.of(request);
            p.setId(null);
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
        if (files != null && !files.isEmpty()) {
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
                    Map<String, String> imageUrlMap = r2StorageService.bulkUpload(imagesToUpload, R2StorageService.PRODUCT_IMAGE_FOLDER + "/" + products.get(i).getId());
                    for (String imageName : imageNamesOfProduct) {
                        ProductImage productImage = new ProductImage();
                        productImage.setProductId(products.get(i).getId());
                        productImage.setUrl(imageUrlMap.get(imageName));
                        productImages.add(productImage);
                    }
                }
            }
            productImageRepository.saveAll(productImages);
        }
        return toResponse(products);
    }

    private void validateProduct(List<ProductRequest> requests) {
        if (requests.isEmpty()) {
            throw new BusinessException("Request list cannot be empty.");
        }
        List<Long> subCategoryIds = requests.stream()
                .map(ProductRequest::getSubCategoryId)
                .distinct()
                .toList();
        if (subCategoryIds.size() != subCategoryRepository.countActiveBySubCategoryIds(subCategoryIds)) {
            throw new BusinessException("One or more sub-categories do not exist or are inactive.");
        }

        List<String> slugs = requests.stream()
                .map(ProductRequest::getSlug)
                .toList();
        if (slugs.size() != requests.size() || productRepository.existsBySlugInAndStatus(slugs, EntityStatus.ACTIVE.getValue())) {
            throw new BusinessException("One or more slugs are duplicate or already exist.");
        }
        List<Long> promotionIds = requests.stream()
                .flatMap(r -> r.getPromotionIds() != null ? r.getPromotionIds().stream() : null)
                .distinct()
                .toList();
        if (promotionIds != null && !promotionIds.isEmpty() && !promotionRepository.existsByIdInAndStatus(promotionIds, EntityStatus.ACTIVE.getValue())) {
            throw new BusinessException("One or more promotions do not exist or are inactive.");
        }
    }

    @Transactional
    public ProductDto updateProduct(String jsonRequests, List<MultipartFile> files) {

        // Parse JSON request
        ProductRequest request = JsonMapperUtils.mapper(jsonRequests, ProductRequest.class);

        // Validate request
        validateProduct(List.of(request));

        // Update product
        Product product = productRepository.findByIdAndActive(request.getId()).orElseThrow(() -> new BusinessException("Product not found"));
        product.setSubCategoryId(request.getSubCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setSlug(request.getSlug());
        product.setStatus(request.getStatus() != null ? request.getStatus() : EntityStatus.ACTIVE.getValue());
        product = productRepository.save(product);


        // Handle promotions
        List<Long> existedPromotionIds = promotionProductRepository.findAllByProductId(product.getId())
                .stream()
                .map(PromotionProduct::getPromotionId)
                .toList();

        List<Long> needDeletedPromotionIds = existedPromotionIds.stream()
                .filter(id -> !request.getPromotionIds().contains(id))
                .toList();
        promotionProductRepository.deleteAllById(needDeletedPromotionIds);

        List<Long> needAddedPromotionIds = request.getPromotionIds().stream()
                .filter(id -> !existedPromotionIds.contains(id))
                .toList();

        List<PromotionProduct> promotionProducts = new ArrayList<>();
        for (Long promotionId : needAddedPromotionIds) {
            PromotionProduct pp = new PromotionProduct(null, promotionId, product.getId());
            promotionProducts.add(pp);
        }
        promotionProductRepository.saveAll(promotionProducts);

        // Handle images
        r2StorageService.deleteImagesByProductId(request.getId());
        List<ProductImage> existingImages = productImageRepository.findAllByProductId(request.getId());
        productImageRepository.deleteAll(existingImages);

        return null;

    }

    @Transactional
    public String delete(List<Long> ids) {
        // Delete products
        List<Product> products = productRepository.findAllByIdIn(ids);
        productRepository.deleteAll(products);

        // Delete product from promotions
        List<PromotionProduct> promotionProducts = promotionProductRepository.findAllByProductIdIn(ids);
        promotionProductRepository.deleteAll(promotionProducts);

        // Handle images
        for(Long id : ids) {
            r2StorageService.deleteImagesByProductId(id);
        }
        List<ProductImage> existingImages = productImageRepository.findAllByProductIdIn(ids);
        productImageRepository.deleteAll(existingImages);

        List<ProductImage> productImages = productImageRepository.findAllByProductIdIn(ids);
        productImageRepository.deleteAll(productImages);

        return "success";
    }
}
