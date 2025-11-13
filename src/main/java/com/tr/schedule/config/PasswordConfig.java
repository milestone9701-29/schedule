package com.tr.schedule.config;

// @Bean
import org.springframework.context.annotation.Bean;

// @Configuration
import org.springframework.context.annotation.Configuration;

// Spring Security : PasswordEncoder, BCryptPWEncoder
// $2<a/b/x/y>$[cost]$[22-character salt][31-character hash] : 보완 필요.
// 기본 10
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration


public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
