package com.mallang.mallnagorder.kiosk.exception;

import com.mallang.mallnagorder.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum KioskExceptionType implements BaseExceptionType{

    KIOSK_NOT_FOUND(HttpStatus.BAD_REQUEST, "KIOSK_001", "해당 키오스크를 찾을 수 없습니다."),
    ACTIVE_KIOSK_EXISTS(HttpStatus.BAD_REQUEST, "KIOSK_002", "활성화된 키오스크가 있어 테이블 수를 변경할 수 없습니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;

    KioskExceptionType(HttpStatus httpStatus, String errorCode, String errorMessage) {
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

