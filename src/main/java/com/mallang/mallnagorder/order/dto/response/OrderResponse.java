package com.mallang.mallnagorder.order.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class OrderResponse {
    private int kioskNumber;
    private List<OrderSummary> orders;

    @Builder
    public OrderResponse(int kioskNumber, List<OrderSummary> orders) {
        this.kioskNumber = kioskNumber;
        this.orders = orders;
    }

    @Getter
    @Builder
    public static class OrderSummary {
        private Long orderId;
        private String createdAt;
        private List<OrderItemSummary> items;
    }

    @Getter
    @Builder
    public static class OrderItemSummary {
        private String menuName;
        private String menuNameEn;
        private BigDecimal menuPrice;
        private int quantity;
    }
}
