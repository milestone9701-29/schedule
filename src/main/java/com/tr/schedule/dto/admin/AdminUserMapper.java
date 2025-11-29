package com.tr.schedule.dto.admin;

import com.tr.schedule.domain.User;
import com.tr.schedule.dto.user.UserSummaryView;
import org.springframework.stereotype.Component;

@Component
public class AdminUserMapper {
    public AdminUserDetailResponse toAdminDetail(User user){
        return new AdminUserDetailResponse(
            UserSummaryView.from(user),
            user.getUpdatedAt()
        );
    }
    public AdminUserSummaryResponse toAdminSummary(User user){
        return new AdminUserSummaryResponse(UserSummaryView.from(user));
    }
}
