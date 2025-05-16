package com.mallang.mallnagorder.menu.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuResponse {
    private Long menuId;
    private String menuName;
    private BigDecimal menuPrice;
    private String imageUrl;
    private Long adminId;
    private List<CategoryInfo> categories;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryInfo {
        private Long categoryId;
        private String categoryName;
    }
}