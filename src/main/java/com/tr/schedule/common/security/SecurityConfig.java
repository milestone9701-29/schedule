package com.tr.schedule.common.security;


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


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled=true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 예외 핸들링.
            .exceptionHandling(ex -> ex
                // 미인증(토큰 없음, 깨짐) -> 401
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                })
                // 인증은 됐으나, 권한 부족 -> 403
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                })
            )

            // --- // 2025-11-18

            .authorizeHttpRequests(auth->auth
                // 인증 불필요
                .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                .requestMatchers("/actuator/health").permitAll() // actuator 2025-11-18

                // ADMIN 전용 영역
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 경로별로 명확히 나뉘어 있어서 순서 영향이 거의 없는 편.
                // MANAGER, ADMIN,  공통 관리 영역("/api/manage/**") : 구현 중.
                .requestMatchers("/api/manage/**").hasAnyRole("MANAGER", "ADMIN")

                // User info
                .requestMatchers("/api/users/me").hasRole("USER")

                // 타인 정보 조회 : MANAGER, ADMIN  : 추가 관리 기능 대비
                .requestMatchers("/api/users/**").hasAnyRole("MANAGER", "ADMIN")

                // 일정, 댓글 : Login 한 User만.
                .requestMatchers("/api/schedules/**").hasRole("USER")
                .requestMatchers("/api/schedules/*/comments/**").hasRole("USER")

                // 인증만 된다면 허용
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
// passwordConfig에 pw encoder 있으니 생략.
// "/api/auth/**", "/h2-console/**" : Authorization Bearer <token> 없을 경우, 401, 403

// /api/admin** : 요청 시 -> JWT filter에서 ROLE_ADMIN이 있는 유저 통과.
// /api/schedule GET : ROLE_USER 만 있어도 통과.
