package com.tr.schedule.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    // HttpStatus.BAD_REQUEST.value()
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C400", "잘못된 요청입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C500", "알 수 없는 오류입니다."),

    // Auth
    AUTH_INVALID_PASSWORD(HttpStatus.FORBIDDEN, "A403", "Password가 올바르지 않습니다."),
    AUTH_INVALID_EMAIL(HttpStatus.FORBIDDEN, "A403", "Email이 올바르지 않습니다."),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A401", "Token이 만료되었습니다."),

    // Schedule
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "S404", "존재하지 않는 일정입니다."),
    SCHEDULE_FORBIDDEN(HttpStatus.FORBIDDEN, "S403", "해당 일정에 접근할 수 없습니다."),
    SCHEDULE_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "S409", "댓글 허용 개수를 초과하였습니다."),

    // Comment
    COMMENT_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "CM409", "댓글 허용 개수를 초과하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;


}
