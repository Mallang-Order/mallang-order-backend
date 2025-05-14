package com.mallang.mallnagorder.order.dto;

import lombok.Getter;

@Getter
public class OrderItemDTO {
    private Long menuId;
    private String name;
    private Integer quantity;
}
