package com.zpi.common.api.dto;

import com.zpi.user.domain.User;
import com.zpi.utils.HashGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class UserDTO {
    @NotNull
    private final String login;
    @NotNull
    private final String password;

    public User toHashedDomain() {
        var generator = new HashGenerator();

        return User.builder()
                .login(generator.generate(login))
                .password(generator.generate(password))
                .build();
    }
}

