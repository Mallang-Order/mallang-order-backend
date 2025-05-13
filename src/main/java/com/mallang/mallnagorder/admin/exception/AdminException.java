package com.mallang.mallnagorder.admin.exception;

import com.mallang.mallnagorder.global.exception.BaseException;
import com.mallang.mallnagorder.global.exception.BaseExceptionType;

public class AdminException extends BaseException {
    private final BaseExceptionType exceptionType;

    public AdminException(BaseExceptionType exceptionType) {
        super(exceptionType.getErrorMessage()); // 부모 생성자 호출
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }

}
