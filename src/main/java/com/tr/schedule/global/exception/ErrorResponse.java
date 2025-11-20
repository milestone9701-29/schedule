package com.tr.schedule.global.exception;

/*JSON : {"code", "msg", "path"}
"code : ErrorCode.code"
"msg : ErrorCode.defaultMessage"
* 추가 설명은 로그에만 남기고, 응답 메시지는 enum 기본 값으로.
"path : request.getRequestURI()"
-> ErrorResponse.of(ErrorCode, path)*/

public record ErrorResponse(String code, String message, String path) {
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(
            errorCode.getCode(),          // "AUTH_401", "JWT_401_EXPIRED" 같은 코드
            errorCode.getDefaultMessage(),// enum 안에 있는 기본 메시지
            path
        );
    }
}
