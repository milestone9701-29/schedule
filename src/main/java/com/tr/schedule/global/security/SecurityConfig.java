package com.tr.schedule.global.security;


import com.tr.schedule.global.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletResponse;
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
    /*
    .csrf(csrf -> csrf.disable()) : 쿠키 세션 쓰지 않고, JWT만 사용하므로 비활성화
     SessionCreationPolicy.STATELESS : HttpSession 미사용. 매 요청마다 JWT로 인증 재구성. */
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter
        ) throws Exception {
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

