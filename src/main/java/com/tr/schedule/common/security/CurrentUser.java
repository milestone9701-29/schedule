package com.tr.schedule.common.security;

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
        // 예외처리 : 서비스 동작단이라 따로 커스터마이징 해야할거 같은데
        if(principal==null) throw new NullPointerException("NPE"); // 403 처리 예정

        // ENUM SET
        Set<Role> rolesCopy=principal.getRoles().isEmpty()
            ? Collections.emptySet()
            : Collections.unmodifiableSet(EnumSet.copyOf(principal.getRoles()));
        // 스냅샷 불변 뷰 : 아니 잘하면서 왜 배운걸 적당적당히 넘기려 해. 이건 나도 안단말야.
        // 근데 EnumSet으로 방어적 복사한다는 개념 자체를 모르니까 도움 받는건데

        return new CurrentUser(
            principal.getId(),
            principal.getEmail(),
            principal.getUsername(),
            rolesCopy
        );
    }

    // 편의 메서드 : 0과 1로 권한 부여
    // 이것도 배틀넷에서 대충 이런 식으로 권한 부여 한다는거 알고 있어서 깊게 가도 괜찮다 한건데
    public boolean hasRole(Role role){
        return roles!=null&&roles.contains(role);
    }

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
