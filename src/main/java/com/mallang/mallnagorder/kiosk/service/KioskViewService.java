package com.mallang.mallnagorder.kiosk.service;

import com.mallang.mallnagorder.category.dto.CategoryWithMenuResponse;
import com.mallang.mallnagorder.kiosk.domain.Kiosk;
import com.mallang.mallnagorder.kiosk.exception.KioskException;
import com.mallang.mallnagorder.kiosk.exception.KioskExceptionType;
import com.mallang.mallnagorder.kiosk.repository.KioskRepository;
import com.mallang.mallnagorder.order.domain.Order;
import com.mallang.mallnagorder.order.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KioskViewService {

    private final KioskRepository kioskRepository;

    public List<CategoryWithMenuResponse> getCategoriesByKiosk(Long kioskId) {
        Kiosk kiosk = kioskRepository.findById(kioskId)
                .orElseThrow(() -> new KioskException(KioskExceptionType.KIOSK_NOT_FOUND));

        return kiosk.getAdmin().getCategories().stream()
                .map(CategoryWithMenuResponse::from)
                .toList();
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderResponse getOrdersByKiosk(Long kioskId) {

        Kiosk kiosk = kioskRepository.findById(kioskId)
                .orElseThrow(() -> new KioskException(KioskExceptionType.KIOSK_NOT_FOUND));

        List<Order> orders = kiosk.getOrders();

        if (orders.isEmpty()) {
            throw new KioskException(KioskExceptionType.ORDER_NOT_FOUND);
        }

        int kioskNumber = orders.get(0).getKiosk().getKioskNumber(); // 같은 키오스크의 주문이므로 하나만 참조

        List<OrderResponse.OrderSummary> orderSummaries = orders.stream()
                .map(order -> OrderResponse.OrderSummary.builder()
                        .orderId(order.getId())
                        .createdAt(order.getCreatedDate().format(formatter))
                        .items(order.getOrderItems().stream()
                                .map(item -> OrderResponse.OrderItemSummary.builder()
                                        .menuName(item.getMenu().getMenuName())
                                        .menuPrice(item.getMenu().getMenuPrice())
                                        .quantity(item.getQuantity())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return OrderResponse.builder()
                .kioskNumber(kioskNumber)
                .orders(orderSummaries)
                .build();
    }

}
