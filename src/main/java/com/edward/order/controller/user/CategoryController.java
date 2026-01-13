package com.edward.order.controller.user;

import com.edward.order.service.CategoryService;
import com.edward.order.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseUtils.success(categoryService.getAllAndActive());
    }

    @GetMapping("/sub-categories")
    public ResponseEntity<?> getAllWithSubCategories() {
        return ResponseUtils.success(categoryService.getAllWithSubCategories());
    }
}
