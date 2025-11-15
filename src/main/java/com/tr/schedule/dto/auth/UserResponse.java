package com.tr.schedule.dto.auth;


import lombok.Value;

import java.time.LocalDateTime;


// 출력 값 : id, username, email, 최초 생성일.
@Value
public class UserResponse {
    Long id;
    String username;
    String email;
    LocalDateTime createdAt;
}
