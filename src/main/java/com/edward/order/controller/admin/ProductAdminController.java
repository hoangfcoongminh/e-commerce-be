package com.edward.order.controller.admin;

import com.edward.order.dto.request.SearchProductRequest;
import com.edward.order.service.admin.ProductAdminService;
import com.edward.order.utils.ResponseUtils;
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

    private final ProductAdminService productAdminService;

    @GetMapping()
    public ResponseEntity<?> getAll(Pageable pageable) {
        return ResponseUtils.success(productAdminService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {
        return ResponseUtils.success(productAdminService.getById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(
            @RequestBody SearchProductRequest request,
            Pageable pageable
    ) {
        return ResponseUtils.success(productAdminService.search(request, pageable));
    }

    @PostMapping("/bulk-create")
    public ResponseEntity<?> create(
            @RequestPart("requests") String requestsJson,
            @RequestPart("files") List<MultipartFile> images
    ) {
        return ResponseUtils.success(productAdminService.createProducts(requestsJson, images));
    }

    @PutMapping()
    public ResponseEntity<?> update(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> images
    ) {
        return ResponseUtils.success(productAdminService.updateProduct(requestJson, images));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @RequestBody List<Long> ids
    ) {
        return ResponseUtils.success(productAdminService.delete(ids));
    }
}
