package com.edward.order.controller.admin;

import com.edward.order.dto.CategoryDto;
import com.edward.order.service.CategoryService;
import com.edward.order.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<?> getAll(
            Pageable pageable
    ) {
        return ResponseUtils.success(categoryService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<?> create(
            @Valid @RequestBody CategoryDto dto
    ) {
        return ResponseUtils.success(categoryService.create(dto));
    }

    @PutMapping()
    public ResponseEntity<?> update(
            @Valid @RequestBody CategoryDto dto
    ) {
        return ResponseUtils.success(categoryService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {
        return ResponseUtils.success(categoryService.delete(id));
    }
}
