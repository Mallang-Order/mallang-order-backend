package com.mallang.mallnagorder.category.dto;

import com.mallang.mallnagorder.category.domain.Category;
import com.mallang.mallnagorder.menu.domain.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class CategoryWithMenuResponse {
    private Long categoryId;
    private String categoryName;
    private String categoryNameEn;
    private List<MenuView> menus;

    public static CategoryWithMenuResponse from(Category category) {
        return CategoryWithMenuResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .categoryNameEn(category.getCategoryNameEn())
                .menus(
                        category.getMenus().stream()
                                .map(MenuView::from)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MenuView {
        private Long menuId;
        private String menuName;
        private String menuNameEn;
        private BigDecimal menuPrice;
        private String imageUrl;

        public static MenuView from(Menu menu) {
            return MenuView.builder()
                    .menuId(menu.getId())
                    .menuName(menu.getMenuName())
                    .menuNameEn(menu.getMenuNameEn())
                    .menuPrice(menu.getMenuPrice())
                    .imageUrl(menu.getImageUrl())
                    .build();
        }
    }
}
