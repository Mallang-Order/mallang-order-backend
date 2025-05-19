package com.mallang.mallnagorder.menu.dto;

import com.mallang.mallnagorder.menu.domain.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class MenuViewResponse {
    private Long menuId;
    private String menuName;
    private String menuNameEn;
    private BigDecimal menuPrice;
    private String imageUrl;

    public static MenuViewResponse from(Menu menu) {
        return MenuViewResponse.builder()
                .menuId(menu.getId())
                .menuName(menu.getMenuName())
                .menuNameEn(menu.getMenuNameEn())
                .menuPrice(menu.getMenuPrice())
                .imageUrl(menu.getImageUrl())
                .build();
    }
}
