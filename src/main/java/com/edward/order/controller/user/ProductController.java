package com.edward.order.controller.user;

import com.edward.order.dto.request.FilterProductRequest;
import com.edward.order.service.admin.ProductAdminService;
import com.edward.order.service.user.ProductUserService;
import com.edward.order.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUserService productUserService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(
            Pageable pageable
    ) {
        return ResponseUtils.success(productUserService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {
        return ResponseUtils.success(productUserService.getById(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter (
            @RequestBody FilterProductRequest request,
            Pageable pageable
    ) {
        return ResponseUtils.success(productUserService.filter(request, pageable));
    }

    @PostMapping("/add-to-cart/{productId}")
    public ResponseEntity<?> addToCart(
            @PathVariable Long productId,
            @RequestParam Integer quantity
    ) {
        return ResponseUtils.success(productUserService.addToCart(productId, quantity));
    }
}
