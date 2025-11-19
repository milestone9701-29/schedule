package com.tr.schedule.global.exception;

public class CommentLimitExceededException extends BusinessException {
    public CommentLimitExceededException(ErrorCode errorCode) {
        super(errorCode);
    }
}
