package com.mallang.mallnagorder.menu.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.admin.exception.AdminException;
import com.mallang.mallnagorder.admin.exception.AdminExceptionType;
import com.mallang.mallnagorder.admin.repository.AdminRepository;
import com.mallang.mallnagorder.category.domain.Category;
import com.mallang.mallnagorder.category.repository.CategoryRepository;
import com.mallang.mallnagorder.global.util.S3Uploader;
import com.mallang.mallnagorder.menu.domain.Menu;
import com.mallang.mallnagorder.menu.dto.MenuRequest;
import com.mallang.mallnagorder.menu.dto.MenuResponse;
import com.mallang.mallnagorder.menu.exception.MenuException;
import com.mallang.mallnagorder.menu.exception.MenuExceptionType;
import com.mallang.mallnagorder.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;
    private final MenuRepository menuRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public MenuResponse createMenu(Long adminId, MenuRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminException(AdminExceptionType.ADMIN_NOT_EXIST));

        // Default 카테고리 확보
        Category defaultCategory = categoryRepository.findByCategoryNameAndAdminId("전체", admin.getId())
                .orElseGet(() -> {
                    Category newDefault = Category.builder()
                            .categoryName("전체")
                            .categoryNameEn("All")
                            .admin(admin)
                            .menus(new ArrayList<>())
                            .build();
                    return categoryRepository.save(newDefault);
                });

        List<Category> categories = new ArrayList<>();
        categories.add(defaultCategory);
        if (request.getCategoryIds() != null) {
            categories.addAll(categoryRepository.findAllById(request.getCategoryIds()));
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

        Menu menu = Menu.builder()
                .menuName(request.getMenuName())
                .menuNameEn(request.getMenuNameEn())
                .menuPrice(request.getMenuPrice())
                .imageUrl(imageUrl)
                .admin(admin)
                .categories(categories)
                .build();

        return toResponse(menuRepository.save(menu));
    }

    @Transactional
    public MenuResponse updateMenu(Long adminId, Long menuId, MenuRequest request) {
        Menu menu = menuRepository.findByIdAndAdminId(menuId, adminId)
                .orElseThrow(() -> new MenuException(MenuExceptionType.MENU_NOT_FOUND));

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

        // 카테고리 처리 동일
        List<Category> categories = new ArrayList<>();
        Category defaultCategory = categoryRepository.findByCategoryNameAndAdminId("전체", menu.getAdmin().getId())
                .orElseGet(() -> {
                    Category newDefault = Category.builder()
                            .categoryName("전체")
                            .categoryNameEn("All")
                            .admin(menu.getAdmin())
                            .build();
                    return categoryRepository.save(newDefault);
                });
        categories.add(defaultCategory);
        if (request.getCategoryIds() != null) {
            categories.addAll(categoryRepository.findAllById(request.getCategoryIds()));
        }
        menu.setCategories(categories);

        return toResponse(menu);
    }

    // 메뉴 삭제
    @Transactional
    public void deleteMenu(Long adminId, Long menuId){
        Menu menu = menuRepository.findByIdAndAdminId(menuId, adminId)
                .orElseThrow(() -> new MenuException(MenuExceptionType.MENU_NOT_FOUND));

        menuRepository.delete(menu);
    }

    public MenuResponse toResponse(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId()) // 주의: getId(), getMenuId() 등 실제 필드명 확인 필요
                .menuName(menu.getMenuName())
                .menuNamEn(menu.getMenuNameEn())
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
