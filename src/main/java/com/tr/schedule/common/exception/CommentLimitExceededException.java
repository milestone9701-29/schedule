package com.tr.schedule.common.exception;

public class CommentLimitExceededException extends BusinessException {
    public CommentLimitExceededException(ErrorCode errorCode) {
        super(errorCode);
    }
}
