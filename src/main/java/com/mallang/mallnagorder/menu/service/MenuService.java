package com.mallang.mallnagorder.menu.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.admin.exception.AdminException;
import com.mallang.mallnagorder.admin.exception.AdminExceptionType;
import com.mallang.mallnagorder.admin.repository.AdminRepository;
import com.mallang.mallnagorder.category.domain.Category;
import com.mallang.mallnagorder.category.repository.CategoryRepository;
import com.mallang.mallnagorder.menu.domain.Menu;
import com.mallang.mallnagorder.menu.dto.MenuRequest;
import com.mallang.mallnagorder.menu.dto.MenuResponse;
import com.mallang.mallnagorder.menu.exception.MenuException;
import com.mallang.mallnagorder.menu.exception.MenuExceptionType;
import com.mallang.mallnagorder.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;
    private final MenuRepository menuRepository;

    // 메뉴 생성
    @Transactional
    public MenuResponse createMenu(MenuRequest request) {
        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new AdminException(AdminExceptionType.ADMIN_NOT_FOUND));

        // 'Default' 카테고리 검색 또는 생성
        Category defaultCategory = categoryRepository.findByCategoryNameAndAdminId("Default", admin.getId())
                .orElseGet(() -> {
                    Category newDefault = Category.builder()
                            .categoryName("Default")
                            .admin(admin)
                            .build();
                    return categoryRepository.save(newDefault);
                });

        // 요청에 따라 선택된 카테고리 추가 (선택적으로 추가)
        List<Category> categories = new ArrayList<>();
        categories.add(defaultCategory); // 항상 전체 포함

        if (request.getCategoryIds() != null) {
            List<Category> extraCategories = categoryRepository.findAllById(request.getCategoryIds());
            categories.addAll(extraCategories);
        }

        // 메뉴 생성
        Menu menu = Menu.builder()
                .menuName(request.getMenuName())
                .menuPrice(request.getMenuPrice())
                .imageUrl(request.getImageUrl())
                .admin(admin)
                .categories(categories)
                .build();

        Menu saved = menuRepository.save(menu);
        return toResponse(saved);

    }

    // 메뉴 삭제
    @Transactional
    public void deleteMenu(Long menuId){
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuExceptionType.MENU_NOT_FOUND));

        menuRepository.delete(menu);

    }

    // 카테고리 아이디로 메뉴 리스트 조회
    @Transactional(readOnly = true)
    public List<MenuResponse> getMenusByCategoryId(Long categoryId) {
        List<Menu> menus = menuRepository.findByCategories_Id(categoryId);
        return menus.stream()
                .map(this::toResponse)
                .toList();
    }

    // 메뉴 아이디로 메뉴 조회
    @Transactional(readOnly = true)
    public MenuResponse getMenuById(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuExceptionType.MENU_NOT_FOUND));
        return toResponse(menu);
    }

    // 메뉴 수정
    @Transactional
    public MenuResponse updateMenu(Long menuId, MenuRequest request) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuExceptionType.MENU_NOT_FOUND));

        // 필드 업데이트
        menu.setMenuName(request.getMenuName());
        menu.setMenuPrice(request.getMenuPrice());
        menu.setImageUrl(request.getImageUrl());

        // 카테고리 업데이트
        List<Category> categories = new ArrayList<>();

        // Default 카테고리 유지
        Category defaultCategory = categoryRepository.findByCategoryNameAndAdminId("Default", menu.getAdmin().getId())
                .orElseGet(() -> {
                    Category newDefault = Category.builder()
                            .categoryName("Default")
                            .admin(menu.getAdmin())
                            .build();
                    return categoryRepository.save(newDefault);
                });
        categories.add(defaultCategory);

        // 추가 카테고리 있으면 더함
        if (request.getCategoryIds() != null) {
            List<Category> extraCategories = categoryRepository.findAllById(request.getCategoryIds());
            categories.addAll(extraCategories);
        }

        menu.setCategories(categories);

        return toResponse(menu);
    }



    public MenuResponse toResponse(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId()) // 주의: getId(), getMenuId() 등 실제 필드명 확인 필요
                .menuName(menu.getMenuName())
                .menuPrice(menu.getMenuPrice())
                .imageUrl(menu.getImageUrl())
                .adminId(menu.getAdmin().getId())
                .categories(menu.getCategories().stream()
                        .map(category -> MenuResponse.CategoryInfo.builder()
                                .categoryId(category.getId())
                                .categoryName(category.getCategoryName())
                                .build())
                        .toList())
                .build();
    }
}
