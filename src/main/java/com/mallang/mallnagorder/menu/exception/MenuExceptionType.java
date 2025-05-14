package com.mallang.mallnagorder.menu.exception;

import com.mallang.mallnagorder.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MenuExceptionType implements BaseExceptionType {

    MENU_NOT_FOUND(HttpStatus.BAD_REQUEST, "MENU_001", "해당 메뉴를 찾을 수 없습니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;


    MenuExceptionType(HttpStatus httpStatus, String errorCode, String errorMessage) {
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
