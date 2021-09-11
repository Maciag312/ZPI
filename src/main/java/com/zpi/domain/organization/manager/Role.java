package com.zpi.domain.organization.manager;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN;
    public String getAuthority() {
        return name();
    }
}