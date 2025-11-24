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

// @Order(1) @Order(2) : 1-2-2-1 : 선입후출

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // JSON WEB TOKEN
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // request header : Authorization
        String header = request.getHeader(AUTHORIZATION_HEADER);

        // header : null != header가 "Bearer "(공백 포함 7 : header.substring(7))을 문자열 토큰에 저장.

        // 1). Authorization Header체크 -> 없으면 다음으로.
        if (header == null || !header.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(AUTHORIZATION_HEADER.length()); // "Bearer "(7자)

        // 2). 토큰 검증(토큰 만료, 위조, 형식 등)
        // jwtTokenProvider가 토큰 서명 유무, 만료, 형식 등을 검사.
        // try -> jwtTokenProvider.getUserId(token)을 userId에 Long으로 저장. : parseClaims(token);
        // getUserId(token) : JWT payload(claims)에서 클레임 꺼내는 역할. : JSON 형태.
        jwtTokenProvider.validateTokenOrThrow(token);

        // 3). UserId 뽑은 후
        Long userId = jwtTokenProvider.getUserId(token);

        // 4). DB에서 실제 User 조회 -> Spring Security 기준 UserDetails interface으로 움직이므로,
        // framework가 기대하는 abstract type에 맞춰 쓰는 것.
        // instanceof CustomUserDetails.
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        // 5). Authentication 생성. Spring Security 표준 Authentication 구현체
        // principal = userDetails
        // credentials = null : PW 다시 비교 하지 않음.
        // authorities = 권한 목록
        UsernamePasswordAuthenticationToken authentication
            = new UsernamePasswordAuthenticationToken(
                userDetails,
            null,
            userDetails.getAuthorities());
        // 요청 정보를 details에 채워두기 : log, 감사 대비.
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // SecurityContext에 authentication 저장.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
    // shouldNotFilter : /api/auth/** ~ Skip
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path=request.getRequestURI();
        return path.startsWith("/api/auth/")  // Signup, Login
            || path.startsWith("/h2-console") // DB
            || path.startsWith("/actuator/health"); // Health Checking용 Endpoint
    }


}
/*
Client -> filterChain
	-> JwtAuthenticationFilter
	-> DispatcherServlet
	-> @Controller, @RestController
 */
