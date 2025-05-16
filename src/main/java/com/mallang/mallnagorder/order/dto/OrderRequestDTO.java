package com.mallang.mallnagorder.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class OrderRequestDTO {
    private Long kioskId;
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;
}
