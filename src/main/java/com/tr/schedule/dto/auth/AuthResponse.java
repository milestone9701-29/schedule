package com.tr.schedule.dto.auth;

public record AuthResponse(
    String token,
    UserResponse user
) {}
