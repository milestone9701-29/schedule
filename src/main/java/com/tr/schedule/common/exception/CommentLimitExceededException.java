package com.tr.schedule.common.exception;

public class CommentLimitExceededException extends RuntimeException {
    public CommentLimitExceededException(String message) {
        super(message);
    }
}
