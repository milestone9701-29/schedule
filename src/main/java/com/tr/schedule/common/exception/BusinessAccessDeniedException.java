package com.tr.schedule.common.exception;

public class BusinessAccessDeniedException extends RuntimeException {
    public BusinessAccessDeniedException(String message) {
        super(message);
    }
}
