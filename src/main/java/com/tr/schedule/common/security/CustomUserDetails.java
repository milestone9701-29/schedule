package com.tr.schedule.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails{
    private final Long id;
    private final String email;
    private final String username;
    private final String password; // passwordHash

    public CustomUserDetails(User user){
        this.id=user.getId();
        this.email=user.getEmail();
        this.username=user.getUsername();
        this.password=user.getPasswordHash();
    }
    public Long getId(){
        return id;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return Collections.emptyList(); // 아직은 권한을 쓰지 않음.
    }
    @Override
    public String getPassword(){
        return password;
    }
    public String getUsername(){
        return email; // email을 principal로
    }
    @Override
    public boolean isAccountNonExpired(){ return true; }
    @Override
    public boolean isAccountNonLocked(){ return true; }
    @Override
    public boolean isCredentialsNonExpired(){ return true; }
    @Override
    public boolean isEnabled(){ return true; }
}
