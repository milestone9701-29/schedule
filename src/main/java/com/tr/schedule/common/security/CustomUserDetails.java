package com.tr.schedule.common.security;

import com.tr.schedule.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails{
    private final Long id;
    private final String email;
    private final String username;
    private final String password; // passwordHash
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user){
        this.id=user.getId();
        this.email=user.getEmail();
        this.username=user.getUsername();
        this.password=user.getPasswordHash();
        this.authorities()=user.getRoles().stream().map(role->new SimpleGrantedAuthority("ROLE_"+role.name())).toList;
    }
    public Long getId(){
        return id;
    }
    @Override
    public String getPassword(){
        return password;
    }
    public String getUsername(){
        return email; //  principal = email
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities; // 아직은 권한을 쓰지 않음.
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
