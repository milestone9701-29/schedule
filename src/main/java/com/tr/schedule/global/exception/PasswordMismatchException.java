package com.tr.schedule.global.exception;

public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
