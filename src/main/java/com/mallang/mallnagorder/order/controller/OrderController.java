package com.mallang.mallnagorder.order.controller;

import com.mallang.mallnagorder.intent.service.IntentRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IntentRouter intentRouter;

}
