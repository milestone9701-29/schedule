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
    // 현재 로그인한 사람이 누구인지 판별하기 위한 식별자 : email로 지정
    // * 필드 이름 그대로 해석해선 안된다. : 인증된 사용자를 대표하는 principal : email로 할 것인지, 다른 무언가로 할 것인지 자유롭게 선택할 수 있다.
    // 따라서 AuthService -> email로 조회하는 것이 이 이유.

    // principal.getId() -> 내부 PK
    // principal.getEmail() -> 로그인용 이메일
    // principal.getUsername() -> 프레임워크 관점의 username(=이메일)

    // UserDetails : Authentication and Authorization.
    @Override public String getPassword(){ return password; }
    @Override public String getUsername(){ return email; } //  principal = email

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
/* The authentication Filter : ProviderManager에 구현되는 AuthenticationManager에 UsernamePasswordAuthenticationToken 전달.
-> ProviderManager는 DaoAuthenticationProvider 타입의 AuthenticationProvider를 사용하도록 구성.
-> DaoAuthenticationProvider는 UserDetailsService에서 UserDetails를 조회
-> 인증에 성공하면 반환되는 인증 : UsernamePasswordAuthenticationToken 타입.
구성된 UserDetailsService에서 반환된, UserDetails인 주체를 가짐.
마지막으로 반환된 UsernamePasswordAuthenticationToken은 인증 필터에 의해 SecurityContextHolder에 설정.
출처 : https://moonsiri.tistory.com/181
*/

