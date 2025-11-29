package com.tr.schedule.dto.auth;

public record SignupResponse(
    String accessToken,
    String refreshToken,
    AuthResult authResult) {
}
