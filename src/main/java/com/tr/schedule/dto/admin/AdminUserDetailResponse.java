package com.tr.schedule.dto.admin;

import com.tr.schedule.dto.user.UserSummaryView;

import java.time.LocalDateTime;


public record AdminUserDetailResponse(
    UserSummaryView user,
    LocalDateTime updatedAt
) {
}
