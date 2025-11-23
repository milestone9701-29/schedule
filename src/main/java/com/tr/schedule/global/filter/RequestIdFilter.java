package com.tr.schedule.global.filter;

import jakarta.servlet.FilterChain;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@Component
public class RequestIdFilter extends OncePerRequestFilter {
    private static final String HEADER_NAME="X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String requestId=request.getHeader(HEADER_NAME);
        if(requestId==null||requestId.isBlank()){
            requestId= UUID.randomUUID().toString();
        }
        try{
            MDC.put("requestId",requestId);
            response.setHeader(HEADER_NAME, requestId);
            filterChain.doFilter(request, response);
        } finally{
            MDC.remove("requestId");
        }
    }
}
/*

1. RequestIdFilter
1). 용도 : Tracking용 tag 붙이기. log. : 이 요청이 어떤 요청인지 추적하기 위한 ID를 강제로 하나(OncePerRequestFilter) 붙인다.
* 즉, 로그 상에서 이 로그들이 같은 요청에서 나온 것인지 묶기 위한 용도.
2). 상세 동작
(1). 클라이언트가 X-Request-Id 헤더를 줄 경우, 사용.
(2). 없으면, 서버가 UUID 하나 새로 발급 : (requestId==null||requestId.isBlank()) requestId=UUID.randomUUID().toString();
(3). 발급한 값을 MDC에 넣어서 전체 로깅에 같이 기록되며, 응답 헤더에도 X-Request-Id로 되돌려준다.

2. OncePerRequestFilter
1). Servlet filter의 일종
2). 한 HTTP 요청 당 딱 한 번만 실행을 보장해주는 템플릿 베이스 클래스.
* filterChain에 여러 filter가 있는데, 동일 요청이 include, forward 등으로 여러 번 내부 Dispatch 될 때가 있다.
이 때, 일반 filter는 Dispatch마다 다시 호출될 수 있는데, OncePerRequestFilter는 같은 요청에 대해 중복 호출되지 않게 막을 수 있다.
-> filterChain 동작, 요청 1번에 1회 실행 보장.
3). 필요 조건
(1). doFilterInternal(request, response, filterChain) @Override.

*/
