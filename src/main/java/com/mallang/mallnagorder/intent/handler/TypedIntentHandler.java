package com.mallang.mallnagorder.intent.handler;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.intent.dto.payload.IntentPayload;

// intent별 처리 핸들러 인터페이스
public interface TypedIntentHandler <T extends IntentPayload> extends IntentHandler {
    void handle(T payload, Admin admin);

    String getSupportedIntent();

    Class<T> getPayloadType();
}
