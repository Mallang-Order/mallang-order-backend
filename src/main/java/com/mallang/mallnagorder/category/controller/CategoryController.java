package com.mallang.mallnagorder.category.controller;

import com.mallang.mallnagorder.admin.dto.AdminDetails;
import com.mallang.mallnagorder.category.dto.CategoryResponse;
import com.mallang.mallnagorder.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@AuthenticationPrincipal AdminDetails adminDetails,
                                                           @RequestBody String categoryName) {

        CategoryResponse response = categoryService.createCategory(categoryName, adminDetails.getAdmin().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created 응답
    }

    // 카테고리 이름 수정
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@AuthenticationPrincipal AdminDetails adminDetails,
                                                           @PathVariable Long categoryId,
                                                           @RequestBody String newName) {

        CategoryResponse response = categoryService.updateCategory(adminDetails.getAdmin().getId(), categoryId, newName);
        return ResponseEntity.ok(response); // 200 OK 응답
    }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal AdminDetails adminDetails,
                                                @PathVariable Long categoryId) {
        categoryService.deleteCategory(adminDetails.getAdmin().getId(), categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content 응답
    }


}
