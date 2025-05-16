package com.mallang.mallnagorder.intent.dto;

public class IntentResponse {
    private String message;

    public IntentResponse(String message) {
        this.message = message;
    }

    public static IntentResponse of(String message) {
        return new IntentResponse(message);
    }
}

