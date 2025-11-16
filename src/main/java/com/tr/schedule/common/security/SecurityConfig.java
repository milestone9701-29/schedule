package com.tr.schedule.common.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain springSecurityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf->csrf.disable())
            .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth->auth
                .requestMatchers(
                    "/api/auth/**",
                    "/h2-console/**" // jdbc:mysql://localhost:3306/schedule_db?useSSL=false&serverTimezone=Asia/Seoul : 나 mysql 씀.
                ).permitAll()
                .anyRequest().authenticated()
            )
            .userDetailsService(customUserDetailsService);
        // h2 콘솔 : frameOptions 무시
        http.headers(headers->headers.frameOptions(frame->frame.sameOrigin()));
        // JWT filter : UsernamePasswordAuthenticationFilter 전에 끼워넣기
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    // passwordConfig에 pw encoder 있으니 생략.
    // "/api/auth/**", "/h2-console/**" : Authorization Bearer <token> 없을 경우, 401, 403
}
