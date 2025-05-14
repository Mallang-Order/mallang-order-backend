package com.mallang.mallnagorder.category.controller;

import com.mallang.mallnagorder.category.dto.CategoryRequest;
import com.mallang.mallnagorder.category.dto.CategoryResponse;
import com.mallang.mallnagorder.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request.getCategoryName(), request.getAdminId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created 응답
    }

    // 카테고리 이름 수정
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId, @RequestBody String newName) {
        CategoryResponse response = categoryService.updateCategory(categoryId, newName);
        return ResponseEntity.ok(response); // 200 OK 응답
    }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content 응답
    }

//    // 카테고리 조회(아이디)
//    @GetMapping("/{categoryId}")
//    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
//        CategoryResponse response = categoryService.getCategoryById(categoryId);
//        return ResponseEntity.ok(response); // 200 OK 응답
//    }
//
//    // 관리자의 모든 카테고리 조회
//    @GetMapping("/admin/{adminId}")
//    public ResponseEntity<List<CategoryResponse>> getCategoriesByAdminId(@PathVariable Long adminId) {
//        List<CategoryResponse> responses = categoryService.getCategoriesByAdminId(adminId);
//        return ResponseEntity.ok(responses); // 200 OK 응답
//    }

}
