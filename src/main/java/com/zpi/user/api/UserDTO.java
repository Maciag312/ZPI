package com.zpi.user.api;

import com.zpi.user.User;
import com.zpi.utils.HashGenerator;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {
    private final String name;
    private final String surname;
    private final String login;
    private final String password;

    public User toHashedDomain() {
        return User.builder()
                .name(HashGenerator.generate(name))
                .surname(HashGenerator.generate(surname))
                .login(HashGenerator.generate(login))
                .password(HashGenerator.generate(password))
                .build();
    }
}

