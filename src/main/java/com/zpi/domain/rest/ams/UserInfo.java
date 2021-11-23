package com.zpi.domain.rest.ams;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class UserInfo {
    @Getter
    private final String email;
    private final List<String> permissions;
    private final List<String> roles;

    public String getPermissions() {
        return String.join(" ", permissions);
    }

    public String getRoles() {
        return String.join(" ", roles);
    }
}
