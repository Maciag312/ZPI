package com.zpi.user.api;

import com.zpi.user.domain.User;
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
        var generator = new HashGenerator();

        return User.builder()
                .name(generator.generate(name))
                .surname(generator.generate(surname))
                .login(generator.generate(login))
                .password(generator.generate(password))
                .build();
    }
}

