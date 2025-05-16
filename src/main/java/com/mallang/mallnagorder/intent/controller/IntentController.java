package com.mallang.mallnagorder.intent.controller;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.intent.dto.IntentWrapperDTO;
import com.mallang.mallnagorder.intent.service.IntentRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/intent")
@RequiredArgsConstructor
public class IntentController {
    private final IntentRouter intentRouter;

    @PostMapping
    public ResponseEntity<Void> handleIntent(@RequestBody IntentWrapperDTO dto,
                                             @AuthenticationPrincipal Admin admin) {
        intentRouter.route(dto, admin);
        return ResponseEntity.ok().build();
    }
}
