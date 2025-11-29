package com.tr.schedule.dto.admin;

import com.tr.schedule.domain.User;
import org.springframework.stereotype.Component;

@Component
public class AdminUserMapper {
    public AdminUserDetailResponse toDetailResponse(User user){
        return new AdminUserDetailResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles(),
            user.isBanned(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    public AdminUserSummaryResponse toSummaryResponse(User user){
        return new AdminUserSummaryResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles(),
            user.isBanned()
        );
    }
}
