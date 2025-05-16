package com.mallang.mallnagorder.intent.except;

import com.mallang.mallnagorder.global.exception.BaseException;
import com.mallang.mallnagorder.global.exception.BaseExceptionType;

public class IntentException extends BaseException {

    private final BaseExceptionType exceptionType;

    public IntentException(BaseExceptionType exceptionType) {
        super(exceptionType.getErrorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return this.exceptionType;
    }
}
