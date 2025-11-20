package com.tr.schedule.global.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

// message + cause 버전, message - only 버전
@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final ErrorCode errorCode;

    public JwtAuthenticationException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode=errorCode;
    }

    public JwtAuthenticationException(ErrorCode errorCode){
        super(errorCode.getDefaultMessage());
        this.errorCode=errorCode;
    }


}
