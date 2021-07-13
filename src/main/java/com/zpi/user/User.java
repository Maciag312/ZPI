package com.zpi.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
    private final String name;
    private final String surname;
    private final String login;
    private final String password;
}
