package com.zpi.domain.organization.manager;

import lombok.Value;

import java.util.List;

@Value
public class Manager {
    String username;
    String password;
    String organizationName;
    List<Role> roles;
}
