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

/*SecurityConfig.java 내용 중
.exceptionHandling(ex->ex
.authenticationEntryPoint(jwtAuthenticationEntryPoint)) < 401
상속 걸어서 재정의
*/
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // JSON 직렬화 MapperClass.
    private final ObjectMapper objectMapper;

    // Security가 인증 실패를 감지했을 때
    /* 예시
    1. JWT FILTER : JwtAuthenticationException -> Security -> commence
    2. SecurityConfig으로 보호한 URL : .authorizeHttpRequests(auth->auth
    .requestMatchers("/api/schedules/**").hasRole("USER"));
    3. 기타 : AuthenticationException(BadCredentials 등)
    */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        ErrorCode errorCode;

        // 커스텀 예외가 맞는지?
        // -> 1. JWT_EXPIRED - JWT_INVALID
        // -> 2. errorCode=ErrorCode.AUTH_TOKEN_EXPIRED (임시)
        if(authenticationException instanceof JwtAuthenticationException jwtAuthenticationException){
            errorCode=jwtAuthenticationException.getErrorCode(); // JWT_EXPIRED - JWT_INVALID
        } else {
            errorCode= ErrorCode.AUTH_TOKEN_EXPIRED; // 401
        }

        // code, message, path -> JSON으로 변환할 DTO 완성.
        ErrorResponse body = ErrorResponse.of(errorCode, request.getRequestURI());


        response.setStatus(errorCode.getStatus().value()); // errorCode 상태.
        response.setContentType("application/json"); // Jackson 형태로
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body); // body 객체를 JSON으로 직렬화 -> HTTP 응답 body로 사용.

        // 조정 필요
        /* 1. 예외 메시지 : 토큰 없음 vs 토큰 만료 구분
        2. Object 주입 방식.
        3. log
         */

    }
}
