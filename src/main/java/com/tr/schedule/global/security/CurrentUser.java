package com.tr.schedule.global.security;

import com.tr.schedule.domain.Role;

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
        // 예외처리 : 따로 커스터마이징 해야할거 같은데
        if(principal==null) throw new NullPointerException("NPE"); // 403 처리 예정

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

    // 편의 메서드 : 0과 1로 권한 부여
    public boolean hasRole(Role role){
        return roles!=null&&roles.contains(role);
    }

    // Controller - Service 정리 중 권한 확인 시 사용 예정.
    public boolean hasAnyRoles(Set<Role> roles){
        if(roles==null||roles.isEmpty()) return false;
        for(Role role:roles){
            if(roles.contains(role)) return true;
        }
        return false;
    }
    public boolean isUser(){ return hasRole(Role.USER); }
    public boolean isManager(){ return hasRole(Role.MANAGER); }
    public boolean isAdmin(){ return hasRole(Role.ADMIN); }
}
