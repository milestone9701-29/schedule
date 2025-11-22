package com.tr.schedule.dto.auth;

import com.tr.schedule.dto.user.UserSummaryResponse;


public record LoginResponse(
    String accessToken,
    String refreshToken,
    UserSummaryResponse user
) {
}
