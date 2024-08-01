package com.scaler.userservice.security.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scaler.userservice.models.Role;
import com.scaler.userservice.models.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonDeserialize
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {
    private String password;
    private String username;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean accountNonLocked;
    private List<GrantedAuthority> authorities;


    public CustomUserDetails(User user) {
        this.password = user.getHashedPassword();
        this.username = user.getEmail();
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
        this.accountNonLocked = true;

        authorities = new ArrayList<>();
        for(Role role: user.getRoles()) {
            authorities.add(new CustomGrantedAuthority(role));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
