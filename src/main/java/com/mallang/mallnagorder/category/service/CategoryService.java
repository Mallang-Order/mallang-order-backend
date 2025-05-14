package com.mallang.mallnagorder.category.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.admin.exception.AdminException;
import com.mallang.mallnagorder.admin.exception.AdminExceptionType;
import com.mallang.mallnagorder.admin.repository.AdminRepository;
import com.mallang.mallnagorder.category.domain.Category;
import com.mallang.mallnagorder.category.dto.CategoryResponse;
import com.mallang.mallnagorder.category.exception.CategoryExceptionType;
import com.mallang.mallnagorder.category.exception.CategoryException;
import com.mallang.mallnagorder.category.repository.CategoryRepository;
import com.mallang.mallnagorder.menu.domain.Menu;
import com.mallang.mallnagorder.menu.repository.MenuRepository;
import org.springframework.transaction.annotation.Transactional; // ✅ 올바른 import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;
    private final MenuRepository menuRepository;

    // 카테고리 생성
    @Transactional
    public CategoryResponse createCategory(String categoryName, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminException(AdminExceptionType.ADMIN_NOT_FOUND));

        // 카테고리 이름 중복 검사
        if (categoryRepository.existsByCategoryNameAndAdminId(categoryName, adminId)) {
                throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME);
        }

        Category category = Category.builder()
                .categoryName(categoryName)
                .admin(admin)
                .menus(new ArrayList<>())  // 메뉴 리스트는 비어 있어도 OK
                .build();

        Category saved = categoryRepository.save(category);

        return toResponse(saved);
    }

    // 카테고리 이름 수정
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, String newName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionType.CATEGORY_NOT_FOUND));

        // 이름 중복 검사
        if (!category.getCategoryName().equals(newName)) {
            if (categoryRepository.existsByCategoryNameAndAdminId(newName, category.getAdmin().getId())) {
                throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME);
            }
            category.setCategoryName(newName);
        }

        return toResponse(category);
    }

    // 카테고리 조회(카테고리 아이디)
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionType.CATEGORY_NOT_FOUND));
        return toResponse(category);
    }

    // 카테고리 조회(관리자 아이디)
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByAdminId(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminException(AdminExceptionType.ADMIN_NOT_FOUND));

        return categoryRepository.findAllByAdmin(admin).stream()
                .map(this::toResponse)
                .toList();
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionType.CATEGORY_NOT_FOUND));

        // 기본 카테고리(전체 메뉴 리스트를 포함)는 삭제 불가능
        if (category.getCategoryName().equals("Default")) {
            throw new CategoryException(CategoryExceptionType.CANNOT_DELETE_DEFAULT_CATEGORY);
        }

        // 해당 카테고리에 속한 메뉴 목록 조회
        List<Menu> menus = menuRepository.findByCategories_Id(categoryId);

        // 각 메뉴에서 카테고리 연결 제거
        for (Menu menu : menus) {
            menu.setCategories(null); // 카테고리 관계를 끊음
        }

        // 카테고리 삭제
        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getCategoryName())
                .adminId(category.getAdmin().getId())
                .build();
    }
}
