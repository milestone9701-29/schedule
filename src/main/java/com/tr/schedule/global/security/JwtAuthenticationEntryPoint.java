package com.tr.schedule.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.ErrorResponse;
import com.tr.schedule.global.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        ErrorCode errorCode;

        if(authenticationException instanceof JwtAuthenticationException jwtAuthenticationException){
            errorCode=jwtAuthenticationException.getErrorCode(); // JWT_EXPIRED - JWT_INVALID
        } else {
            errorCode= ErrorCode.AUTH_TOKEN_EXPIRED; // 401
        }
        ErrorResponse body = ErrorResponse.of(errorCode, request.getRequestURI());

        response.setStatus(errorCode.getStatus().value()); // errorCode 상태.
        response.setContentType("application/json"); // Jackson 형태로
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);

        // 조정 필요
    }
}
