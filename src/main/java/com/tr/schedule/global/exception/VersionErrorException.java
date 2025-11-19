package com.tr.schedule.global.exception;

public class VersionErrorException extends BusinessException {
    public VersionErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
