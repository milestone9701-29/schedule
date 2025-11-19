package com.tr.schedule.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/*
400 = BAD_REQUEST = 요청이 이상함.
401 = UNAUTHORIZED = 유효한 인증 자격 증명 없음.
403 = FORBIDDEN = 너의 권한이 아님.
404 = NOT_FOUND = 그런 리소스 없음.
409 = CONFLICT = 버전/상태 충돌.
*/
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    // HttpStatus.BAD_REQUEST.value()
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C400", "잘못된 요청입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C500", "알 수 없는 오류입니다."),

    // Auth
    AUTH_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "A401-01", "Password가 올바르지 않습니다."),
    AUTH_INVALID_EMAIL(HttpStatus.UNAUTHORIZED, "A401-02", "Email이 올바르지 않습니다."),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A401-03", "Token이 만료되었습니다."),

    // Schedule
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "S404-01", "존재하지 않는 일정입니다."),
    SCHEDULE_FORBIDDEN(HttpStatus.FORBIDDEN, "S403-01", "해당 일정에 접근할 수 없습니다."),
    SCHEDULE_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "S409-01", "댓글 허용 개수를 초과하였습니다."),
    SCHEDULE_VERSION_CONFLICT(HttpStatus.CONFLICT, "S409-02", "다른 요청에 의해 일정이 먼저 수정되었습니다."), // 우선순위 개념.

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CM404-01", "존재하지 않는 댓글입니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "CM403-01", "해당 댓글에 접근할 수 없습니다."),
    COMMENT_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "CM409-01", "댓글 허용 개수를 초과하였습니다."),
    COMMENT_VERSION_CONFLICT(HttpStatus.CONFLICT, "CM409-02", "다른 요청에 의해 댓글이 먼저 수정되었습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U404-01", "존재하지 않는 사용자입니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "U403-01", "해당 사용자에 접근할 수 없습니다.");



    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

}
