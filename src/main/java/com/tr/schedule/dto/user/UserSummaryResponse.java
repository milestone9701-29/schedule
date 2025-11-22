package com.tr.schedule.dto.user;


import java.time.LocalDateTime;


// 출력 값 : id, username, email, 최초 생성일.

public record UserSummaryResponse(Long id, String username, String email, LocalDateTime createdAt) {}
