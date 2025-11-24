package com.tr.schedule.dto.auth;

import java.time.LocalDateTime;

public record SignupResult(
    Long id,
    String username,
    String email,
    LocalDateTime createdAt) {}
