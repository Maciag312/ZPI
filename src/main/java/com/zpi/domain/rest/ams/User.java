package com.zpi.domain.rest.ams;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Builder
@EqualsAndHashCode
public class User {
    private final String login;
    private final String password;
}
