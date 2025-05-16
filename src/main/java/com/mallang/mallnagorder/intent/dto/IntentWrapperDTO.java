package com.mallang.mallnagorder.intent.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class IntentWrapperDTO { // 클라이언트로부터 intent와 payload
    private String intent; // 의도
    private JsonNode payload; // intent 별 세부 데이터
}
