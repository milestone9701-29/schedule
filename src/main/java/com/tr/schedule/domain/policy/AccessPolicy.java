package com.tr.schedule.domain.policy;

import com.tr.schedule.domain.Comment;
import com.tr.schedule.domain.Role;
import com.tr.schedule.domain.Schedule;
import com.tr.schedule.global.exception.BusinessAccessDeniedException;
import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.security.CurrentUser;

public final class AccessPolicy {
    private AccessPolicy() {}

    public static void ensureCanAccessSchedule(CurrentUser currentUser, Schedule schedule){
        // ADMIN, MANAGER : 타인의 일정
        // User Entity로 권한 꺼내는 방법 : if(user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.MANAGER)){ return; }
        if(currentUser.roles().hasAny(Role.ADMIN,Role.MANAGER)){ return; }
        // User : Owner 일치 시에만 허용.
        if (!currentUser.id().equals(schedule.getOwner().getId())) { throw new BusinessAccessDeniedException(ErrorCode.SCHEDULE_ACCESS_FORBIDDEN); }
    }
    public static void ensureCanAccessComment(Schedule schedule, CurrentUser currentUser, Comment comment) {
        // ADMIN, MANAGER : 같은 Schedule 안의 Comment이면, 누구의 것이든 수정 및 삭제가 가능.
        if (currentUser.roles().hasAny(Role.ADMIN,Role.MANAGER)) {
            if (!schedule.getId().equals(comment.getSchedule().getId())) {
                throw new BusinessAccessDeniedException(ErrorCode.COMMENT_FORBIDDEN);
            }
            return;
        }

        // 저자 체크
        if (!currentUser.id().equals(comment.getAuthor().getId())) {
            throw new BusinessAccessDeniedException(ErrorCode.COMMENT_FORBIDDEN);
        }
        // schedule 간의 id 체크
        if (!schedule.getId().equals(comment.getSchedule().getId())) {
            throw new BusinessAccessDeniedException(ErrorCode.COMMENT_FORBIDDEN);
        }
    }
}
