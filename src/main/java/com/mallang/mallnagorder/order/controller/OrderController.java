package com.mallang.mallnagorder.order.controller;

import com.mallang.mallnagorder.admin.dto.AdminDetails;
import com.mallang.mallnagorder.order.dto.OrderRequestDTO;
import com.mallang.mallnagorder.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(
            @AuthenticationPrincipal AdminDetails adminDetails,
            @RequestBody OrderRequestDTO orderRequestDTO
    ) {
        orderService.placeOrder(adminDetails.getAdmin(), orderRequestDTO);
        return ResponseEntity.ok("주문이 성공적으로 접수되었습니다.");
    }

    @DeleteMapping("/by-kiosk/{kioskId}")
    public ResponseEntity<String> deleteOrdersByKiosk(
            @AuthenticationPrincipal AdminDetails adminDetails,
            @PathVariable Long kioskId
    ) {
        orderService.deleteOrdersByKiosk(adminDetails.getAdmin().getId(), kioskId);
        return ResponseEntity.ok("결제 완료: 해당 테이블의 주문이 모두 삭제되었습니다.");
    }

}
