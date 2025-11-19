package com.tr.schedule.global.security;

import com.tr.schedule.domain.Role;
import com.tr.schedule.global.exception.BusinessAccessDeniedException;
import com.tr.schedule.global.exception.ErrorCode;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public record CurrentUser(
    Long id,
    String email,
    String username,
    Set<Role> roles
) {
    // CustomUserDetails -> CurrentUser
    public static CurrentUser from(CustomUserDetails principal) {
        if(principal==null) throw new BusinessAccessDeniedException(ErrorCode.USER_FORBIDDEN); // 403

        // Collections.unmodifiableSet(EnumSet.copyOf())); : EnumSet 스냅샷 + 불변 뷰
        Set<Role> rolesCopy=principal.getRoles().isEmpty()
            ? Collections.emptySet()
            : Collections.unmodifiableSet(EnumSet.copyOf(principal.getRoles()));

        return new CurrentUser(
            principal.getId(),
            principal.getEmail(),
            principal.getUsername(),
            rolesCopy
        );
    }

    // 하나 : null 검사,
    public boolean hasRole(Role role){
        return roles!=null&&roles.contains(role);
    }

    // varargs : 여러 개 중 하나라도.
    public boolean hasAnyRoles(Role... candidates){
        if(roles==null||roles.isEmpty()||candidates==null||candidates.length==0) return false;
        for(Role candidate:candidates){
            if(roles.contains(candidate)) return true;
        }
        return false;
    }

    public boolean isUser(){ return hasRole(Role.USER); }
    public boolean isManager(){ return hasRole(Role.MANAGER); }
    public boolean isAdmin(){ return hasRole(Role.ADMIN); }
}
