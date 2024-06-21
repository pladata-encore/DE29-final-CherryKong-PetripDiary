package com.example.finalproject.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.finalproject.model.entity.UserEntity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthUserDto implements UserDetails {

    private UserEntity userEntity;

    // 권한(들)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            
            @Override
            public String getAuthority() {
                return userEntity.getRole();
            }
        });
        
        return authorities;
    }

    @Override
    public String getPassword() {
        
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        
        return userEntity.getUserid();
    }

    @Override
    public boolean isAccountNonExpired() {
        
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        
        return false;
    }

    @Override
    public boolean isEnabled() {
        
        return false;
    }

    
}
