package com.tr.schedule.dto.auth;

import java.time.LocalDateTime;

public record AuthResult(
    Long id,
    String username,
    String email,
    Boolean isBanned,
    LocalDateTime createdAt) {}
