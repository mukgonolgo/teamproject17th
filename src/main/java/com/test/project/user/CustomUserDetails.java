package com.test.project.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final SiteUser siteUser;
    private final Collection<? extends GrantedAuthority> authorities;

    // 생성자 추가
    public CustomUserDetails(SiteUser siteUser, Collection<? extends GrantedAuthority> authorities) {
        this.siteUser = siteUser;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return siteUser.getUsername();
    }

    @Override
    public String getPassword() {
        return siteUser.getPassword();
    }

    public int getApprovalStatus() {
        return siteUser.getApprovalStatus();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
