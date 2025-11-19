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
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user){
        this.id=user.getId();
        this.email=user.getEmail();
        this.username=user.getUsername();
        this.password=user.getPasswordHash();
        this.roles=user.getRoles(); // Collections.unmodifiableSet(roles);

        this.authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .toList();
    }

    // Service : Get(Read)
    public Long getId(){ return id; }
    public String getEmail(){ return email; }
    public Set<Role> getRoles(){ return roles; }

    // UserDetails : Authentication and Authorization.
    @Override
    public String getPassword(){ return password; }
    public String getUsername(){ return username; } //  principal = email
    @Override public Collection<? extends GrantedAuthority> getAuthorities(){ return authorities; }

    @Override public boolean isAccountNonExpired(){ return true; }
    @Override public boolean isAccountNonLocked(){ return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled(){ return true; }
}
