package com.tr.schedule.dto.auth;

import com.tr.schedule.domain.User;

import com.tr.schedule.dto.user.UserSummaryView;
import org.springframework.stereotype.Component;

// 25-11-16 : 이거 건드린게 오늘 제일 잘한 일인 듯? Mapper Class
@Component
public class AuthMapper{

    public User ofSignUp(String encodedPassword, SignupRequest request){// username, email, pw
        return User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(encodedPassword)
            .build();
    }
    public AuthResult toAuthResult(User user) {
        return new AuthResult(UserSummaryView.from(user));
    }
}
