package com.mallang.mallnagorder.order.controller;

import com.mallang.mallnagorder.admin.dto.AdminDetails;
import com.mallang.mallnagorder.admin.dto.StoreInfoResponse;
import com.mallang.mallnagorder.kiosk.domain.Kiosk;
import com.mallang.mallnagorder.order.domain.Order;
import com.mallang.mallnagorder.order.dto.request.OrderRequest;
import com.mallang.mallnagorder.order.dto.response.OrderResponse;
import com.mallang.mallnagorder.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        orderService.createOrder(request);
        return ResponseEntity.ok("주문이 성공적으로 접수되었습니다.");
    }

    // 테이블 모든 주문 삭제
    @DeleteMapping("/by-kiosk/{kioskNumber}")
    public ResponseEntity<String> deleteOrdersByKiosk(
            @AuthenticationPrincipal AdminDetails adminDetails,
            @PathVariable int kioskNumber
    ) {
        orderService.deleteOrdersByKiosk(adminDetails.getAdmin().getId(), kioskNumber);
        return ResponseEntity.ok("결제 완료: 해당 테이블의 주문이 모두 삭제되었습니다.");
    }
}
