package com.tr.schedule.global.security;

import com.tr.schedule.domain.Role;
import com.tr.schedule.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;


public class CustomUserDetails implements UserDetails{
    private final Long id;
    private final String email;
    private final String username;
    private final String password; // passwordHash
    private final Set<Role> roles;

    // org.springframework.security.core
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user){
        this.id=user.getId();
        this.email=user.getEmail();
        this.username=user.getUsername();
        this.password=user.getPasswordHash();
        this.roles=user.getRoles(); // Collections.unmodifiableSet(roles);

        // org.springframework.security.core.authority.SimpleGrantedAuthority :
        // 도메인 단계 Role -> SimpleGrantedAuthority("ROLE_" + name) 을 거쳐서 Spring Security : GrantedAuthority("ROLE_USER")로 Adapting.
        this.authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .toList(); // +16
    }

    // Service : Get(Read)
    public Long getId(){ return id; }
    public String getEmail(){ return email; }
    public Set<Role> getRoles(){ return roles; }


    // SecurityContextHolder.getContext().getAuthentication().getPrincipal()
    // -> principal : Authentication -> 현재 로그인 객체.

    // UserDetails : Authentication and Authorization.
    @Override
    public String getPassword(){ return password; }
    public String getUsername(){ return email; } //  principal = email

    // Generic Invariance -> Producer Extends, Consumer Super
    // Child : SimpleGrantedAuthority => Parent :GrantedAuthority
    // Child : Collection<SimpleGrantedAuthority> != Parent : Collection<GrantedAuthority>
    // getAuthorities -> <? extends GrantedAuthority> -> 어떠한 Type이든 return 가능.
    @Override public Collection<? extends GrantedAuthority> getAuthorities(){ return authorities; }

    // return !user.isLocked(); return user.isActive(); 등.
    // 유저 계정 관리 빌드업 : 계정 잠금/비활성화 같은 기능
    @Override public boolean isAccountNonExpired(){ return true; }
    @Override public boolean isAccountNonLocked(){ return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled(){ return true; }
}
