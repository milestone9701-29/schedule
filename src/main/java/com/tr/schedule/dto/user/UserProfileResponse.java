package com.tr.schedule.dto.user;

import java.time.LocalDateTime;


// 기능 추가 대비용.
public record UserProfileResponse(Long id, String username, String email, LocalDateTime createdAt) {
}
