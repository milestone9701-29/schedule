package com.tr.schedule.dto.auth;

import lombok.Value;

@Value
public record AuthResponse {
    String token;
    UserResponse user;
}
