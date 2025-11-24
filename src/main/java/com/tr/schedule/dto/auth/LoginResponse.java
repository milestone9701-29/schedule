package com.tr.schedule.dto.auth;


public record LoginResponse(
    String accessToken,
    String refreshToken,
    SignupResult signupResult
) {
}
