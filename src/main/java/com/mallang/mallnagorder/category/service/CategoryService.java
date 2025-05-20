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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;
    private final MenuRepository menuRepository;

    // 카테고리 생성
    @Transactional
    public CategoryResponse createCategory(String categoryName, String categoryNameEn, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminException(AdminExceptionType.ADMIN_NOT_EXIST));

        // 카테고리 이름 중복 검사
        if (categoryRepository.existsByCategoryNameAndAdminId(categoryName, adminId)) {
            throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME);
        }
        if (categoryRepository.existsByCategoryNameEnAndAdminId(categoryNameEn, adminId)) {
            throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME_EN);
        }

        // 중복 없는 메뉴 리스트 생성
        Set<Menu> menuSet = new LinkedHashSet<>();

        Category category = Category.builder()
                .categoryName(categoryName)
                .categoryNameEn(categoryNameEn)
                .admin(admin)
                .menus(new ArrayList<>(menuSet)) // 초기엔 빈 Set -> List
                .build();

        Category saved = categoryRepository.save(category);

        return toResponse(saved);
    }


    @Transactional
    public CategoryResponse updateCategory(Long adminId, Long categoryId, String newName, String newNameEn) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionType.CATEGORY_NOT_FOUND));

        // 본인을 제외한 중복 여부 검사
        if (categoryRepository.existsByCategoryNameAndAdminIdAndIdNot(newName, adminId, categoryId)) {
            throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME);
        }
        if (categoryRepository.existsByCategoryNameEnAndAdminIdAndIdNot(newNameEn, adminId, categoryId)) {
            throw new CategoryException(CategoryExceptionType.ALREADY_EXIST_NAME_EN);
        }

        category.setCategoryName(newName);
        category.setCategoryNameEn(newNameEn);

        // 중복 제거 후 메뉴 세팅 (기존 메뉴 유지)
        Set<Menu> distinctMenus = new LinkedHashSet<>(category.getMenus());
        category.setMenus(new ArrayList<>(distinctMenus));

        return toResponse(category);
    }


    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long adminId, Long categoryId) {
        Category category = categoryRepository.findByIdAndAdminId(categoryId, adminId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionType.CATEGORY_NOT_FOUND));

        // 기본 카테고리(전체 메뉴 리스트를 포함)는 삭제 불가능
        if (category.getCategoryName().equals("전체")) {
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
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .categoryNameEn(category.getCategoryNameEn())
                .adminId(category.getAdmin().getId())
                .build();
    }
}
