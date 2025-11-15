package com.tr.schedule.dto.auth;

import com.tr.schedule.domain.User;

import org.springframework.stereotype.Component;


@Component
public class AuthMapper{

    public User ofSignUp(String encodedPassword, SignupRequest request){// username, email, pw
        return User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(encodedPassword)
            .build();
    }

    public UserResponse toResponse(User user){ // 출력 값
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}
