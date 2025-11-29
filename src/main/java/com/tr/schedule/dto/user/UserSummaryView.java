package com.tr.schedule.dto.user;

import com.tr.schedule.domain.Role;
import com.tr.schedule.domain.User;

import java.time.LocalDateTime;
import java.util.Set;

public record UserSummaryView(Long id,
                             String username,
                             String email,
                             Set<Role> roles,
                             Boolean isBanned,
                             LocalDateTime createdAt){
    public static UserSummaryView from(User user) {
        return new UserSummaryView(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.roleSet().asUnmodifiableSet(),
            user.isBanned(),
            user.getCreatedAt()
        );
    }
}
