package com.mallang.mallnagorder.menu.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.admin.exception.AdminException;
import com.mallang.mallnagorder.admin.exception.AdminExceptionType;
import com.mallang.mallnagorder.admin.repository.AdminRepository;
import com.mallang.mallnagorder.ai.service.AdminPayloadService;
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

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;
    private final S3Uploader s3Uploader;
    private final AdminPayloadService adminPayloadService;

    @Transactional
    public MenuResponse createMenu(Long adminId, MenuRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminException(AdminExceptionType.ADMIN_NOT_EXIST));

        if (menuRepository.existsByMenuNameAndAdminId(request.getMenuName(), adminId)) {
            throw new MenuException(MenuExceptionType.ALREADY_EXIST_NAME);
        }
        if (menuRepository.existsByMenuNameEnAndAdminId(request.getMenuNameEn(), adminId)) {
            throw new MenuException(MenuExceptionType.ALREADY_EXIST_NAME_EN);
        }

        String imageUrl = uploadImageOrDefault(request);

        Set<Category> categories = new LinkedHashSet<>();
        Category defaultCategory = getOrCreateDefaultCategory(admin);
        List<Long> requestedCategoryIds = request.getCategoryIds();
        if (requestedCategoryIds != null) {
            categories.addAll(categoryRepository.findAllById(requestedCategoryIds));
        }
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

        Menu savedMenu = menuRepository.save(menu);

        adminPayloadService.generateAndForward(adminId);

        return toResponse(savedMenu);
    }


    @Transactional
    public MenuResponse updateMenu(Long adminId, Long menuId, MenuRequest request) {
        Menu menu = menuRepository.findByIdAndAdminId(menuId, adminId)
                .orElseThrow(() -> new MenuException(MenuExceptionType.MENU_NOT_FOUND));

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
            String imageUrl = uploadImageOrDefault(request);
            menu.setImageUrl(imageUrl);
        }

        Set<Category> categories = new LinkedHashSet<>();
        Category defaultCategory = getOrCreateDefaultCategory(menu.getAdmin());
        List<Long> requestedCategoryIds = request.getCategoryIds();
        if (requestedCategoryIds != null) {
            categories.addAll(categoryRepository.findAllById(requestedCategoryIds));
        }
        if (requestedCategoryIds == null || !requestedCategoryIds.contains(defaultCategory.getId())) {
            categories.add(defaultCategory);
        }

        menu.setCategories(new ArrayList<>(categories));

        Menu updatedMenu = menuRepository.save(menu);

        adminPayloadService.generateAndForward(adminId);

        return toResponse(updatedMenu);
    }


    private String uploadImageOrDefault(MenuRequest request) {
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            if (request.getImage().getSize() > MAX_FILE_SIZE) {
                throw new MenuException(MenuExceptionType.IMAGE_TOO_LARGE);
            }
            try {
                return s3Uploader.upload(request.getImage(), "menu");
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }
        return "https://mallangkiosk-menu-images.s3.ap-northeast-2.amazonaws.com/%E1%84%86%E1%85%A1%E1%86%AF%E1%84%85%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B5.png";
    }

    // ✅ 기본 카테고리 처리 메서드
    private Category getOrCreateDefaultCategory(Admin admin) {
        return categoryRepository.findByCategoryNameAndAdminId("전체", admin.getId())
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .categoryName("전체")
                        .categoryNameEn("All")
                        .admin(admin)
                        .menus(new ArrayList<>())
                        .build()));
    }

    @Transactional
    public void deleteMenu(Long adminId, Long menuId) {
        Menu menu = menuRepository.findByIdAndAdminId(menuId, adminId)
                .orElseThrow(() -> new MenuException(MenuExceptionType.MENU_NOT_FOUND));

        boolean hasOrders = orderItemRepository.existsByMenuId(menu.getId());
        if (hasOrders) {
            throw new MenuException(MenuExceptionType.MENU_HAS_ORDER);
        }

        menuRepository.delete(menu);

        // 예외 발생해도 삭제는 이미 완료된 상태이므로 필요시 별도 트랜잭션으로 분리도 고려 가능
        adminPayloadService.generateAndForward(adminId);
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