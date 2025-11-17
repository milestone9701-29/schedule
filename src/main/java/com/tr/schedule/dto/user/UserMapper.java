package com.tr.schedule.dto.user;

import com.tr.schedule.domain.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {
    public UserProfileResponse toUserProfileResponse(User user){ // 출력 값
        return new UserProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}
