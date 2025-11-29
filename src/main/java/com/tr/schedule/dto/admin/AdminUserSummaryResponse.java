package com.tr.schedule.dto.admin;

import com.tr.schedule.domain.Role;

import java.util.Set;

public record AdminUserSummaryResponse(
    Long id,
    String email,
    String username,
    Set<Role> roles,
    boolean isBanned
) {
}
