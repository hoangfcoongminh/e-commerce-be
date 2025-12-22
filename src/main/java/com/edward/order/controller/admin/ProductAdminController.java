package com.edward.order.controller.admin;

import com.edward.order.dto.request.CreateProductRequest;
import com.edward.order.dto.request.SearchProductRequest;
import com.edward.order.service.ProductService;
import com.edward.order.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PostMapping("/bulk-create")
    public ResponseEntity<?> create(
            @RequestPart("requests") String requestsJson,
            @RequestPart("files") List<MultipartFile> images
    ) {
        return ResponseUtils.success(productService.createProducts(requestsJson, images));
    }
}
