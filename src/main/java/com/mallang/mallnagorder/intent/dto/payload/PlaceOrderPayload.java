package com.mallang.mallnagorder.intent.dto.payload;

import com.mallang.mallnagorder.order.dto.OrderItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlaceOrderPayload implements IntentPayload {
    private Long kioskId;
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;
}
