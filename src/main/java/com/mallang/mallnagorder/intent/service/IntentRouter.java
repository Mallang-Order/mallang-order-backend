package com.mallang.mallnagorder.intent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.intent.dto.IntentWrapperDTO;
import com.mallang.mallnagorder.intent.dto.payload.IntentPayload;
import com.mallang.mallnagorder.intent.handler.TypedIntentHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class IntentRouter {

    private final ObjectMapper objectMapper;
    private final List<TypedIntentHandler<?>> handlers;

    public void route(IntentWrapperDTO wrapper, Admin admin) {
        String intent = wrapper.getIntent();

        // 핸들러 찾기
        TypedIntentHandler<?> rawHandler = handlers.stream()
                .filter(h -> h.getSupportedIntent().equals(intent))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 intent입니다: " + intent));

        // 제네릭 분리 메서드로 안전하게 위임
        routeTyped(rawHandler, wrapper.getPayload(), admin);
    }

    // 제네릭 처리 분리
    @SuppressWarnings("unchecked")
    private <T extends IntentPayload> void routeTyped(
            TypedIntentHandler<?> rawHandler,
            JsonNode rawPayload,
            Admin admin
    ) {
        // 강제 캐스팅 (타입 안전성을 위해 타입은 분명히 확인되었음)
        TypedIntentHandler<T> handler = (TypedIntentHandler<T>) rawHandler;

        Class<T> payloadType = handler.getPayloadType();
        T payload = objectMapper.convertValue(rawPayload, payloadType);

        handler.handle(payload, admin);
    }

}
