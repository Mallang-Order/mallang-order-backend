package com.mallang.mallnagorder.menu.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;


@Getter
public class MenuRequest {
    private String menuName;
    private BigDecimal menuPrice;
    private String imageUrl;
    private Long adminId;
    private List<Long> categoryIds;
}
