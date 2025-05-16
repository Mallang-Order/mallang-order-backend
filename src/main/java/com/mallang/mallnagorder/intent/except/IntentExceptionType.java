package com.mallang.mallnagorder.intent.except;

import com.mallang.mallnagorder.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum IntentExceptionType implements BaseExceptionType {
    INTENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "INTENT_001", "지원하지 않는 intent입니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;


    IntentExceptionType(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
