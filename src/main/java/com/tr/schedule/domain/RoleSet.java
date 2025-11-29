package com.tr.schedule.domain;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public final class RoleSet {
    private final EnumSet<Role> roles;

    private RoleSet(EnumSet<Role> roles) {
        this.roles=roles;
    }

    // empty()
    public static RoleSet empty(){
        return new RoleSet(EnumSet.noneOf(Role.class));
    }
    /*
     * 1. public static RoleSet of(Set<Role> src){}
     * 1). 역할 : 외부에서 들어오는 날 것 Set<Role> src를 받은 다음, 우리 domain 규칙이 적용된 RoleSet 세계로 들여보내는 입구
     * -> of(..), from(..) 등의 팩토리 네이밍이 자연스러움. */
    // 정적 팩토리 메서드(진입로)
    public static RoleSet of(Set<Role> src){
        return (src==null||src.isEmpty())
            ? empty()
            : new RoleSet(EnumSet.copyOf(src)); // InvariantHelper
    }
    // 2. varargs
    public static RoleSet of(Role... src) {
        if (src==null||src.length==0) {
            return empty();
        }
        EnumSet<Role> set = EnumSet.noneOf(Role.class);
        Collections.addAll(set, src);
        return new RoleSet(set);
    }

    // -- 조회, 검증 메서드 -- //

    // 3. has : 한 역할.
    public boolean has(Role role){
        return role!=null&&roles.contains(role);
    }

    // 4. 여러 후보 중 하나라도.
    public boolean hasAny(Role... candidates){
        if(roles.isEmpty()||candidates==null||candidates.length==0){ return false; }
        for(Role c:candidates){
            if(has(c)) return true;
        }
        return false;
    }

    // 5. requiredRoles ⊆ roles ? : true : false
    public boolean canAccessAll(Set<Role> required){ return roles.containsAll(required); }
    // 6. 이 역할들 중 최소 하나를 가져야 한다.
    public boolean canAccessAny(Set<Role> required){ return hasAny(required.toArray(Role[]::new)); }

    public boolean isUser(){ return has(Role.USER); }
    public boolean isManager(){ return has(Role.MANAGER); }
    public boolean isAdmin(){ return has(Role.ADMIN); }

    // 6. unmodifiableSet
    public Set<Role> asUnmodifiableSet(){ return Collections.unmodifiableSet(EnumSet.copyOf(roles)); }
}
