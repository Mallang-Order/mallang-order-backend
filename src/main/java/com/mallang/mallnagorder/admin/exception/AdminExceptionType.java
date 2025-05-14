package com.mallang.mallnagorder.admin.exception;

import com.mallang.mallnagorder.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum AdminExceptionType implements BaseExceptionType {

    ADMIN_NOT_FOUND(HttpStatus.BAD_REQUEST, "ADMIN_001", "가게 정보를 찾을 수 없습니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;


    AdminExceptionType(HttpStatus httpStatus, String errorCode, String errorMessage) {
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
    }}
