package com.tr.schedule.global.exception;

public class BusinessAccessDeniedException extends BusinessException {
    public BusinessAccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
