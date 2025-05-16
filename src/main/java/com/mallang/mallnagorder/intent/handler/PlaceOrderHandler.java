package com.mallang.mallnagorder.intent.handler;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.intent.dto.payload.PlaceOrderPayload;
import com.mallang.mallnagorder.order.dto.OrderRequestDTO;
import com.mallang.mallnagorder.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 주문 처리 intent 핸들러
@Component
@RequiredArgsConstructor
public class PlaceOrderHandler implements TypedIntentHandler<PlaceOrderPayload> {

    private final OrderService orderService;

    @Override
    public String getSupportedIntent() {
        return "place_order";
    }

    @Override
    public Class<PlaceOrderPayload> getPayloadType() {
        return PlaceOrderPayload.class;
    }

    @Override
    public void handle(PlaceOrderPayload payload, Admin admin) {
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .kioskId(payload.getKioskId())
                .totalPrice(payload.getTotalPrice())
                .items(payload.getItems())
                .build();
        orderService.placeOrder(dto, admin);
    }
}
