package com.mallang.mallnagorder.order.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.kiosk.domain.Kiosk;
import com.mallang.mallnagorder.kiosk.exception.KioskException;
import com.mallang.mallnagorder.kiosk.repository.KioskRepository;
import com.mallang.mallnagorder.menu.domain.Menu;
import com.mallang.mallnagorder.menu.exception.MenuException;
import com.mallang.mallnagorder.menu.repository.MenuRepository;
import com.mallang.mallnagorder.order.domain.Order;
import com.mallang.mallnagorder.order.domain.OrderItem;
import com.mallang.mallnagorder.order.dto.OrderItemDTO;
import com.mallang.mallnagorder.order.dto.OrderRequestDTO;
import com.mallang.mallnagorder.order.repository.OrderItemRepository;
import com.mallang.mallnagorder.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.mallang.mallnagorder.kiosk.exception.KioskExceptionType.KIOSK_NOT_FOUND;
import static com.mallang.mallnagorder.menu.exception.MenuExceptionType.MENU_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final KioskRepository kioskRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public void placeOrder(OrderRequestDTO dto, Admin admin) {
        Kiosk kiosk = kioskRepository.findById(dto.getKioskId())
                .orElseThrow(() -> new KioskException(KIOSK_NOT_FOUND));

        Order order = Order.builder()
                .kiosk(kiosk)
                .admin(admin)
                .totalPrice(dto.getTotalPrice())
                .build();

        orderRepository.save(order);

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Menu menu = menuRepository.findById(itemDTO.getMenuId())
                    .orElseThrow(() -> new MenuException(MENU_NOT_FOUND));

            BigDecimal subtotal = menu.getMenuPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menu(menu)
                    .quantity(itemDTO.getQuantity())
                    .subtotal(subtotal)
                    .build();

            orderItemRepository.save(orderItem);
        }
    }
}
