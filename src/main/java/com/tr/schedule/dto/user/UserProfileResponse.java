package com.tr.schedule.dto.user;

import java.time.LocalDateTime;

public record UserProfileResponse(
    Long id,
    String username,
    String email,
    String profileImageUrl,
    String bio,
    Long version,
    LocalDateTime createdAt) {}
