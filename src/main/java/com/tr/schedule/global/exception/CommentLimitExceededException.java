package com.tr.schedule.global.exception;

// 쓸 예정.
public class CommentLimitExceededException extends BusinessException {
    public CommentLimitExceededException(ErrorCode errorCode) {
        super(errorCode);
    }
}
