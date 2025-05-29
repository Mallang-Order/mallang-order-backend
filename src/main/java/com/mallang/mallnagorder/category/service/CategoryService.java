package com.mallang.mallnagorder.category.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.admin.exception.AdminException;
import com.mallang.mallnagorder.admin.exception.AdminExceptionType;
import com.mallang.mallnagorder.admin.repository.AdminRepository;
import com.mallang.mallnagorder.ai.service.AdminPayloadService;
import com.mallang.mallnagorder.category.domain.Category;
import com.mallang.mallnagorder.category.dto.CategoryResponse;
import com.mallang.mallnagorder.category.exception.CategoryExceptionType;
import com.mallang.mallnagorder.category.exception.CategoryException;
import com.mallang.mallnagorder.category.repository.CategoryRepository;
import com.mallang.mallnagorder.menu.domain.Menu;
import com.mallang.mallnagorder.menu.repository.MenuRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;
    private final MenuRepository menuRepository;
    private final AdminPayloadService adminPayloadService;

    // 카테고리 생성
    @Transactional
    public CategoryResponse createCategory(String categoryName, String categoryNameEn, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminException(AdminExceptionType.ADMIN_NOT_EXIST));

        if (categoryRepository.existsByCategoryNameAndAdminId(categoryName, adminId)) {
            throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME);
        }
        if (categoryRepository.existsByCategoryNameEnAndAdminId(categoryNameEn, adminId)) {
            throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME_EN);
        }

        Category category = Category.builder()
                .categoryName(categoryName)
                .categoryNameEn(categoryNameEn)
                .admin(admin)
                .menus(Collections.emptyList()) // 초기에는 메뉴 없이
                .build();

        Category saved = categoryRepository.save(category);
        adminPayloadService.generateAndForward(adminId);

        return toResponse(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long adminId, Long categoryId, String newName, String newNameEn) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionType.CATEGORY_NOT_FOUND));

        if (categoryRepository.existsByCategoryNameAndAdminIdAndIdNot(newName, adminId, categoryId)) {
            throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME);
        }
        if (categoryRepository.existsByCategoryNameEnAndAdminIdAndIdNot(newNameEn, adminId, categoryId)) {
            throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME_EN);
        }

        category.setCategoryName(newName);
        category.setCategoryNameEn(newNameEn);

        // 중복 제거 (불필요하지만 안정성 보장)
        List<Menu> distinctMenus = new ArrayList<>(new LinkedHashSet<>(category.getMenus()));
        category.setMenus(distinctMenus);

        adminPayloadService.generateAndForward(adminId);
        return toResponse(category);
    }


    // 카테고리 삭제
    private static final String DEFAULT_CATEGORY_NAME = "전체";

    @Transactional
    public void deleteCategory(Long adminId, Long categoryId) {
        Category category = categoryRepository.findByIdAndAdminId(categoryId, adminId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionType.CATEGORY_NOT_FOUND));

        if (DEFAULT_CATEGORY_NAME.equals(category.getCategoryName())) {
            throw new CategoryException(CategoryExceptionType.CANNOT_DELETE_DEFAULT_CATEGORY);
        }

        List<Menu> menus = menuRepository.findByCategories_Id(categoryId);
        for (Menu menu : menus) {
            // 기존 categories 에서 해당 category만 제거
            List<Category> updated = menu.getCategories().stream()
                    .filter(c -> !c.getId().equals(categoryId))
                    .collect(Collectors.toList());
            menu.setCategories(updated);
        }

        categoryRepository.delete(category);
        adminPayloadService.generateAndForward(adminId);
    }


    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .categoryNameEn(category.getCategoryNameEn())
                .adminId(category.getAdmin().getId())
                .build();
    }
}
