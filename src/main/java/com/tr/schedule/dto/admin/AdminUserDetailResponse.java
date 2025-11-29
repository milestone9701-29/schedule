package com.tr.schedule.dto.admin;

import com.tr.schedule.domain.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record AdminUserDetailResponse(
    Long id,
    String username,
    String email,
    Set<Role> roles,
    boolean isBanned,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
