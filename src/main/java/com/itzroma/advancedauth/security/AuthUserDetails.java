package com.itzroma.advancedauth.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Builder
public class AuthUserDetails implements UserDetails, OAuth2User {
    @Getter
    private String firstName;

    @Getter
    private String lastName;

    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;
    private String password;

    @Getter
    private String email;

    private Boolean enabled;

    @Getter
    private String imageUrl;

    @Getter
    private AuthProvider authProvider;

    @Override
    public String getName() {
        return firstName + (lastName.isEmpty() ? "" : " " + lastName);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes != null ? attributes : Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : Collections.emptySet();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
