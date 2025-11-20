package com.tr.schedule.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;


@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                                   HttpServletResponse response,
                                   AccessDeniedException accessDeniedException) throws IOException{
        ErrorCode errorCode = ErrorCode.AUTH_TOKEN_EXPIRED;

        ErrorResponse body = ErrorResponse.of(errorCode, request.getRequestURI());

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);

    }
}
