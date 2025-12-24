package com.edward.order.controller.admin;

import com.edward.order.dto.SubCategoryDto;
import com.edward.order.dto.request.SearchSubCategoryRq;
import com.edward.order.service.SubCategoryService;
import com.edward.order.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/sub-categories")
@RequiredArgsConstructor
public class SubCategoryAdminController {

    private final SubCategoryService subCategoryService;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseUtils.success(subCategoryService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestBody SearchSubCategoryRq request,
            Pageable pageable) {
        return ResponseUtils.success(subCategoryService.search(request, pageable));
    }

    @PostMapping()
    public ResponseEntity<?> create(
            @Valid @RequestBody SubCategoryDto dto
    ) {
        return ResponseUtils.success(subCategoryService.create(dto));
    }

    @PutMapping()
    public ResponseEntity<?> update(
            @Valid @RequestBody SubCategoryDto dto
    ) {
        return ResponseUtils.success(subCategoryService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {
        return ResponseUtils.success(subCategoryService.delete(id));
    }
}
