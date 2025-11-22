package com.tr.schedule.dto.auth;

import com.tr.schedule.dto.user.UserSummaryResponse;

public record SignupResponse(
    String accessToken,
    String refreshToken,
    UserSummaryResponse userSummaryResponse) {
}
