package com.college.resource.security;

import com.college.resource.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String role;
    private final boolean enabled;

    public UserPrincipal(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPasswordHash();
        this.role = user.getRole().getRoleName();
        this.enabled = user.getStatus().name().equals("ACTIVE");
    }

    public Long getUserId() { return userId; }
    public String getRole()  { return role; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public String getPassword()   { return password; }
    @Override public String getUsername()   { return username; }
    @Override public boolean isEnabled()    { return enabled; }
    @Override public boolean isAccountNonExpired()   { return true; }
    @Override public boolean isAccountNonLocked()    { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
}
