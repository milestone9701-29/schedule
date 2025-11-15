package com.tr.schedule.common.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
