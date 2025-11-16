package com.tr.schedule.dto.auth;


import java.time.LocalDateTime;


// 출력 값 : id, username, email, 최초 생성일.

public record UserResponse(Long id, String username, String email, LocalDateTime createdAt) {}
