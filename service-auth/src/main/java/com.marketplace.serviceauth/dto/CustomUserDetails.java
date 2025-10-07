package com.marketplace.serviceauth.dto;

import com.marketplace.serviceauth.entity.Seller;
import com.marketplace.serviceauth.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Seller seller;

    public CustomUserDetails(User user, Seller seller) {
        this.user = user;
        this.seller = seller;
    }

    public CustomUserDetails(User user) {
        this.user = user;
        this.seller = null;
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getSellerId() {
        if (seller != null) {
            return seller.getId();
        }

        return null;
    }

    public boolean isSeller() {
        return seller != null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
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
        return user.isEnabled();
    }

}
