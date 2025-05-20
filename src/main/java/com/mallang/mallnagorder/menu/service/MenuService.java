package com.mallang.mallnagorder.menu.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.admin.exception.AdminException;
import com.mallang.mallnagorder.admin.exception.AdminExceptionType;
import com.mallang.mallnagorder.admin.repository.AdminRepository;
import com.mallang.mallnagorder.category.domain.Category;
import com.mallang.mallnagorder.category.exception.CategoryException;
import com.mallang.mallnagorder.category.exception.CategoryExceptionType;
import com.mallang.mallnagorder.category.repository.CategoryRepository;
import com.mallang.mallnagorder.global.util.S3Uploader;
import com.mallang.mallnagorder.menu.domain.Menu;
import com.mallang.mallnagorder.menu.dto.MenuRequest;
import com.mallang.mallnagorder.menu.dto.MenuResponse;
import com.mallang.mallnagorder.menu.exception.MenuException;
import com.mallang.mallnagorder.menu.exception.MenuExceptionType;
import com.mallang.mallnagorder.menu.repository.MenuRepository;
import com.mallang.mallnagorder.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public MenuResponse createMenu(Long adminId, MenuRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminException(AdminExceptionType.ADMIN_NOT_EXIST));


        // 메뉴 이름 중복 검사
        if (menuRepository.existsByMenuNameAndAdminId(request.getMenuName(), adminId)) {
            throw new MenuException(MenuExceptionType.ALREADY_EXIST_NAME);
        }
        if (menuRepository.existsByMenuNameEnAndAdminId(request.getMenuNameEn(), adminId)) {
            throw new MenuException(MenuExceptionType.ALREADY_EXIST_NAME_EN);
        }

        String imageUrl;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                imageUrl = s3Uploader.upload(request.getImage(), "menu");
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        } else {
            imageUrl = "https://mallangkiosk-menu-images.s3.ap-northeast-2.amazonaws.com/%E1%84%86%E1%85%A1%E1%86%AF%E1%84%85%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B5.png"; // 기본 이미지 URL
        }

        // 카테고리 중복 제거 처리
        Set<Category> categories = new LinkedHashSet<>();

        Category defaultCategory = categoryRepository.findByCategoryNameAndAdminId("전체", admin.getId())
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .categoryName("전체")
                        .categoryNameEn("All")
                        .admin(admin)
                        .menus(new ArrayList<>())
                        .build()));

        // 요청한 카테고리 ID 목록 가져오기
        List<Long> requestedCategoryIds = request.getCategoryIds();
        if (requestedCategoryIds != null) {
            categories.addAll(categoryRepository.findAllById(requestedCategoryIds));
        }

        // 전체 카테고리가 이미 포함되어 있지 않으면 추가
        if (requestedCategoryIds == null || !requestedCategoryIds.contains(defaultCategory.getId())) {
            categories.add(defaultCategory);
        }

        Menu menu = Menu.builder()
                .menuName(request.getMenuName())
                .menuNameEn(request.getMenuNameEn())
                .menuPrice(request.getMenuPrice())
                .imageUrl(imageUrl)
                .admin(admin)
                .categories(new ArrayList<>(categories))
                .build();

        return toResponse(menuRepository.save(menu));
    }


    @Transactional
    public MenuResponse updateMenu(Long adminId, Long menuId, MenuRequest request) {
        Menu menu = menuRepository.findByIdAndAdminId(menuId, adminId)
                .orElseThrow(() -> new MenuException(MenuExceptionType.MENU_NOT_FOUND));

        // 메뉴 이름 중복 검사 (자기 자신 제외)
        if (menuRepository.existsByMenuNameAndAdminIdAndIdNot(request.getMenuName(), adminId, menuId)) {
            throw new MenuException(MenuExceptionType.ALREADY_EXIST_NAME);
        }
        if (menuRepository.existsByMenuNameEnAndAdminIdAndIdNot(request.getMenuNameEn(), adminId, menuId)) {
            throw new MenuException(MenuExceptionType.ALREADY_EXIST_NAME_EN);
        }

        menu.setMenuName(request.getMenuName());
        menu.setMenuNameEn(request.getMenuNameEn());
        menu.setMenuPrice(request.getMenuPrice());

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = s3Uploader.upload(request.getImage(), "menu");
                menu.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }

        // 카테고리 중복 제거 처리
        Set<Category> categories = new LinkedHashSet<>();

        Category defaultCategory = categoryRepository.findByCategoryNameAndAdminId("전체", menu.getAdmin().getId())
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .categoryName("전체")
                        .categoryNameEn("All")
                        .admin(menu.getAdmin())
                        .menus(new ArrayList<>())
                        .build()));

        List<Long> requestedCategoryIds = request.getCategoryIds();
        if (requestedCategoryIds != null) {
            categories.addAll(categoryRepository.findAllById(requestedCategoryIds));
        }

        if (requestedCategoryIds == null || !requestedCategoryIds.contains(defaultCategory.getId())) {
            categories.add(defaultCategory);
        }

        menu.setCategories(new ArrayList<>(categories));

        return toResponse(menu);
    }

    @Transactional
    public void deleteMenu(Long adminId, Long menuId) {
        boolean hasOrders = orderItemRepository.existsByMenuId(menuId);
        if (hasOrders) {
            throw new MenuException(MenuExceptionType.MENU_HAS_ORDER);
        }

        Menu menu = menuRepository.findByIdAndAdminId(menuId, adminId)
                .orElseThrow(() -> new MenuException(MenuExceptionType.MENU_NOT_FOUND));

        menuRepository.delete(menu);
    }

    private String getImageUrl(MenuRequest request) {
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                return s3Uploader.upload(request.getImage(), "menu");
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }
        return "https://mallangkiosk-menu-images.s3.ap-northeast-2.amazonaws.com/%E1%84%86%E1%85%A1%E1%86%AF%E1%84%85%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B5.png";
    }

    public MenuResponse toResponse(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .menuName(menu.getMenuName())
                .menuNameEn(menu.getMenuNameEn())
                .menuPrice(menu.getMenuPrice())
                .imageUrl(menu.getImageUrl())
                .adminId(menu.getAdmin().getId())
                .categories(menu.getCategories().stream()
                        .map(category -> MenuResponse.CategoryInfo.builder()
                                .categoryId(category.getId())
                                .categoryName(category.getCategoryName())
                                .categoryNameEn(category.getCategoryNameEn())
                                .build())
                        .toList())
                .build();
    }
}