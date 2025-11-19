package com.tr.schedule.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// OncePerRequestFilter : 요청 당 한 번 실행되는 Spring 기본 필터 베이스
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // JSON WEB TOKEN
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // request header : Authorization
        String header = request.getHeader("Authorization");

        // header : null != header가 "Bearer "(공백 포함 7 : header.substring(7))을 문자열 토큰에 저장.
        if(header!=null&&header.startsWith("Bearer ")){
            String token=header.substring(7);

            // jwtTokenProvider가 토큰 서명 유무, 만료, 형식 등을 검사.
            // true -> jwtTokenProvider.getUserId(token)을 userId에 Long으로 저장.
            // getUserId(token) : JWT payload(claims)에서 클레임 꺼내는 역할. : JSON 형태.
            if(jwtTokenProvider.validateToken(token)){
                Long userId=jwtTokenProvider.getUserId(token);

                // DB에서 실제 User를 조회 -> CustomUserDetails : 이건 왜 CustomerUserDetails가 아니라 부모에 넣은거지?
                UserDetails userDetails=customUserDetailsService.loadUserById(userId);

                // 어떻게 구현체 이름이 유저이름비번인증토큰 ㅋㅋ
                // Spring Security 표준 Authentication 구현체
                // principal = userDetails
                // credentials = null : PW 다시 비교 하지 않음.
                // authorities = 권한 목록
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());

                // 요청 정보를 details에 채워두기 : log, 감사 대비.
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // SecurityContext에 authentication 저장.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
/*
Client -> filterChain
	-> JwtAuthenticationFilter
	-> DispatcherServlet
	-> @Controller, @RestController
 */
/* 할 일
예외/실패 처리 shouldNotFilter 단락평가
 */
