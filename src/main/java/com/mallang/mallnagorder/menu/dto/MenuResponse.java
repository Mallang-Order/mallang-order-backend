package com.mallang.mallnagorder.menu.dto;

import com.mallang.mallnagorder.category.domain.Category;
import com.mallang.mallnagorder.menu.domain.Menu;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuResponse {
    private Long menuId;
    private String menuName;
    private String menuNamEn;
    private BigDecimal menuPrice;
    private String imageUrl;
    private Long adminId;
    private List<CategoryInfo> categories;

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .menuName(menu.getMenuName())
                .menuNamEn(menu.getMenuNameEn())
                .menuPrice(menu.getMenuPrice())
                .imageUrl(menu.getImageUrl())
                .adminId(menu.getAdmin().getId())
                .categories(
                        menu.getCategories().stream()
                                .map(category -> new CategoryInfo(category.getId(), category.getCategoryName(),category.getCategoryName()))
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryInfo {
        private Long categoryId;
        private String categoryName;
        private String categoryNameEn;
    }
}
