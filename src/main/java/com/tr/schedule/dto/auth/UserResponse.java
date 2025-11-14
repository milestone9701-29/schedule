package com.tr.schedule.dto.auth;

import com.tr.schedule.domain.User;

import lombok.Value;

import java.time.LocalDateTime;


// 출력 값 : id, username, email, 최초 생성일.
@Value
public class UserResponse {
    Long id;
    String username;
    String email;
    LocalDateTime createdAt;


    // 정적 팩토리 메서드
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}
