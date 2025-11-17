package com.tr.schedule.common.exception;

public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
