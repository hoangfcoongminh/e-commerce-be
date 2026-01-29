package com.edward.order.controller.admin;

import com.edward.order.dto.request.CreatePromotionRequest;
import com.edward.order.service.admin.PromotionService;
import com.edward.order.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping("/{slug}")
    public ResponseEntity<?> getPromotionBySlug(
            @PathVariable String slug,
            Pageable pageable
    ) {
        return ResponseUtils.success(promotionService.getProductsInPromotion(slug, pageable));
    }

    @PostMapping()
    public ResponseEntity<?> createPromotion(
            @Valid @RequestBody CreatePromotionRequest request
    ) {
        return ResponseUtils.success(promotionService.createPromotion(request));
    }
}
