package com.tr.schedule.common.exception;

public class BusinessAccessDeniedException extends BusinessException {
    public BusinessAccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
