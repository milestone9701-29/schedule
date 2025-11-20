package com.tr.schedule.global.exception;

public record ErrorResponse(String code, String message, String path) {
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(
            errorCode.getCode(),          // "AUTH_401", "JWT_401_EXPIRED" 같은 코드
            errorCode.getDefaultMessage(),// enum 안에 있는 기본 메시지
            path
        );
    }
}
