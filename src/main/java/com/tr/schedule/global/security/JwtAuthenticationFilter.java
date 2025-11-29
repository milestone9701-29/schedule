package com.tr.schedule.global.security;

import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
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
        // 1. 경우의 수
        // 1). header==null : 아예 Authorization header가 없는 경우 : public 접근 또는 login 하지 않은 요청.
        // 2). !header.startsWith(AUTHORIZATION_HEADER_PREFIX) : Authorization header는 있으나, Bearer 가 아닌 경우.
        // -> filterChain.doFilter(request, response) : SecurityContext에 아무 것도 안 넣고, 다른 Security 필터가 처리.
        // * shouldNotFilter()
        if (header == null || !header.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(AUTHORIZATION_HEADER_PREFIX.length()); // "Bearer "(7자)

        // 2). 토큰 검증(토큰 만료, 위조, 형식 등)
        // jwtTokenProvider가 토큰 서명 유무, 만료, 형식 등을 검사.
        // try -> jwtTokenProvider.getUserId(token)을 userId에 Long으로 저장. : parseClaims(token);
        // getUserId(token) : JWT payload(claims)에서 클레임 꺼내는 역할. : JSON 형태.
        Claims claims = jwtTokenProvider.validateAndGetToken(token);

        // type access
        String type = claims.get("type", String.class);

        if(!"access".equals(type)){
            throw new JwtAuthenticationException(ErrorCode.JWT_INVALID_TYPE);
        }

        // 3). UserId 뽑은 후
        Long userId = jwtTokenProvider.getUserId(token);

        // 4). DB에서 실제 User 조회 -> Spring Security 기준 UserDetails interface으로 움직이므로,
        // framework가 기대하는 abstract type에 맞춰 쓰는 것.
        // instanceof CustomUserDetails.
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        /* 5). Authentication 생성. Spring Security 표준 Authentication 구현체
        2. UsernamePasswordAuthenticationToken -> 로그인된 인증 객체 역할
        1). Spring Security : 로그인 한 사람 : Authentication Type으로 관리.
        2). 그 중에서, 가장 흔한 구현체가 UsernamePasswordAuthenticationToken.
        3). 용도
        (1). 로그인 시도할 때 : username + password 들고 있는 상태.
        (2). 인증 성공 이후 : UserDetails, Authorization 들고 있는 상태.
        * JWT 필터 : 토큰 검증 -> userId -> DB에서 UserDetails 꺼내기 -> 로그인 확인을 Security에 알림 */
        // principal = userDetails : 누가 로그인 했는지 ? -> 주체. : SecurityContextHolder
        // -> @AuthenticationPrincipal 또는 @AuthUser resolver가 controller에 전달.
        // credentials = null : password : login 시점에 Password 검증이 이미 끝났고, JWT는 한 번 인증된 결과만 들고 온다.
        // 즉, 다시 PW 비교 할 필요 없으므로, null로 둠.
        // authorities = 권한, Role 목록 : hasRole("USER"), @PreAuthorize("hasRole('ADMIN')")
        UsernamePasswordAuthenticationToken authentication
            = new UsernamePasswordAuthenticationToken(
                userDetails,
            null,
            userDetails.getAuthorities());
        // 3. setDetails()
        // 1). 부가정보 : remote IP, session ID 등
        // 2). 응용 :
        // (1). audit log : 로그 감사
        // (2). 어디서 로그인 했는지?
        // (3). 보안 정책
        // -> log, 추적할 때 쓸 수 있는 MetaData.
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // SecurityContext에 authentication 저장. : 이 요청의 SecurityContext에는 Authentication 상태.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
    // 1. shouldNotFilter : /api/auth/** ~ Skip
    // 1). JwtAuthenticationFilter 자체를 건너 뛸 것인지 결정.
    // 2). true : 해당 요청은 JWT 파싱, 검증을 아예 하지 않는다. 따라서 SecurityContext도 건드리지 않으며, 다음 필터로 보낸다.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/")
            || path.startsWith("/actuator/health")
            || path.startsWith("/h2-console");
    }
}
/*
Client -> filterChain
	-> JwtAuthenticationFilter
	-> DispatcherServlet
	-> @Controller, @RestController
 */
