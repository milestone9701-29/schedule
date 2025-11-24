package com.tr.schedule.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
URL 기반 인가 : ant matcher
METHOD 기반 인가
*/

// 한 서버에 보안 정책이 다른 둘을 공존시키는 방법 : SecurityFilterChain을 여러 개 쓰는 것.
// 다른 형태의 보안 정책이 필요한 경우에 따른 filter 분리 및 Order로 순번 처리에 대해 고민해볼 것.
// Authorization 상수 또는 배열화
@Configuration
@EnableWebSecurity // Enable Spring Security filter Chain
@EnableMethodSecurity(prePostEnabled=true) // @PreAuthorize, @PostAuthorize 같은 메서드 인가 허용.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /*JwtAuthenticationFilter를 Bean에 등록 -> filterChain에 주입.
    -> Authorization : Bearer (7자 문자열) 토큰 -> 검증 -> SecurityContext 세팅 담당.*/
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    /*  Spring Security filterChain을 어떻게 만들 것인지 설정하는 Builder
    1. 서순
    1). Application 시작 -> Spring이 HttpSecurity를 하나 만들어서 이 Method에 넣어줌 -> .csrf() .sessionManagement() .authorizeHttpRequests() 체이닝 호출
    -> filterChain 구성, Session 쓸 것인지, csrf 쓸 것인지, 어떤 URL에 어떤 권한을 부여할 것인지.
    2). http.build() : SecurityFilterChain을 만들어 Bean으로 내보냄.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter
        ) throws Exception {

        /*
        .csrf(csrf -> csrf.disable()) : 쿠키 + 세션 기반 로그인에서 의미 있는 공격 벡터 :
         * 쿠키 세션 쓰지 않고, JWT만 사용하므로 비활성화.
         SessionCreationPolicy.STATELESS : HttpSession 만들지 않음. 매 요청마다 JWT로 인증 재구성.
         -> 서버가 “이 유저 로그인 상태야”를 서버 메모리(HttpSession)에 안 들고 간다. */
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Security 단계에서의 예외 처리.
            // 문제 : response.sendError(status)로 상태 코드만 제시
            // -> ErrorResponse + ErrorCode를 Security 단계 또한 통일
            .exceptionHandling(ex -> ex
                    // jwtAuthenticationEntryPoint implements AuthenticationEntryPoint
                    // ->  미인증(토큰 없음, 깨짐) -> authenticationEntryPoint -> 401
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // 인증은 됐으나, 권한 부족 -> accessDeniedHandler -> 403
                .accessDeniedHandler(jwtAccessDeniedHandler)
                )
            // --- // 2025-11-18
            // hasRole("권한명") : 접근 권한
            // hayAnyRole("권한명1", "권한명2") : 접근 권한
            .authorizeHttpRequests(auth->auth
                // 인증 불필요 :  /api/auth/**, /h2-console/** 권한 허용(permitAll())
                .requestMatchers("/api/auth/**", "/h2-console/**", "/actuator/health").permitAll() // 2025-11-19

                // ADMIN 전용 영역 : 추가 필요.
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // MANAGER, ADMIN,  공통 관리 영역("/api/manage/**") : 구현 중.
                .requestMatchers("/api/manage/**").hasAnyRole("MANAGER", "ADMIN")

                // User info : USER
                .requestMatchers("/api/users/me").hasRole("USER")

                // 타인 정보 조회 : MANAGER, ADMIN  : 추가 관리 기능 대비
                .requestMatchers("/api/users/**").hasAnyRole("MANAGER", "ADMIN")

                // 일정, 댓글 : Login 한 User만.
                .requestMatchers("/api/schedules/**").hasRole("USER")

                // anyRequest().authenticated() : 지정안한 나머지 URL(anyRequest()) : 로그인만 돼 있으면 모두 허용(authenticated())
                .anyRequest().authenticated()
            )

            // .addFilterBefore
            // 1. jwtAuthenticationFilter 실행 : Security Context
            // 2. UsernamePasswordAuthenticationFilter 실행 : Form Login 등 다른 인증 방식이 있다면 실행.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

