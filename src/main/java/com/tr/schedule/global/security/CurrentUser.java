package com.tr.schedule.global.security;

import com.tr.schedule.domain.Role;
import com.tr.schedule.domain.RoleSet;
import com.tr.schedule.global.exception.BusinessAccessDeniedException;
import com.tr.schedule.global.exception.ErrorCode;

public record CurrentUser(
    Long id,
    String email,
    String username,
    RoleSet roles
) {
    // CustomUserDetails -> CurrentUser
    public static CurrentUser from(CustomUserDetails principal) {
        if(principal==null) throw new BusinessAccessDeniedException(ErrorCode.USER_FORBIDDEN); // 403

        return new CurrentUser(
            principal.getId(),
            principal.getEmail(),
            principal.getUsername(),
            principal.getRoles()
        );
    }

    public boolean hasRole(Role role){
        return roles.has(role);
    }

    // varargs : 여러 개 중 하나라도.
    public boolean hasAnyRoles(Role... candidates){ return this.roles.hasAny(candidates); }

    public boolean isUser(){ return roles.isUser();}
    public boolean isManager(){ return roles.isManager(); }
    public boolean isAdmin(){ return roles.isAdmin(); }
}
