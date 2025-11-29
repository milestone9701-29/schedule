package com.tr.schedule.domain.policy;

import com.tr.schedule.domain.User;
import com.tr.schedule.global.exception.BusinessAccessDeniedException;
import com.tr.schedule.global.exception.ErrorCode;

public final class UserStatusPolicy{
    private UserStatusPolicy() {}  // new 못하게 막기
    public static void ensureNotBanned(User user){
        if(user.isBanned()){ throw new BusinessAccessDeniedException(ErrorCode.USER_BANNED); } // 403
    }
}
