package com.tr.schedule.dto.auth;


// 주렁주렁 토큰
// service, 보안 계층 등 내부에서 사용.
// AuthService.login() 리턴값
// TokenService.refresh()
// filter, resolver 등에서 사용.
public record AuthTokens(
    String accessToken,
    String refreshToken
) {}
