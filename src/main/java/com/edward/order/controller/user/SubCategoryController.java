package com.edward.order.controller.user;

import com.edward.order.service.SubCategoryService;
import com.edward.order.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sub-categories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseUtils.success(subCategoryService.getAllAndActive());
    }
}
