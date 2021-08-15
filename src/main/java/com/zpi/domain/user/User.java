package com.zpi.domain.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class User {
    private final String login;
    private final String password;
}
