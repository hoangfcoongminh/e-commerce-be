package com.edward.order.controller.admin;

import com.edward.order.dto.request.SearchProductRequest;
import com.edward.order.service.ProductService;
import com.edward.order.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<?> getAll(Pageable pageable) {
        return ResponseUtils.success(productService.getAll(pageable));
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(
            @RequestBody SearchProductRequest request,
            Pageable pageable
    ) {
        return ResponseUtils.success(productService.search(request, pageable));
    }
}
