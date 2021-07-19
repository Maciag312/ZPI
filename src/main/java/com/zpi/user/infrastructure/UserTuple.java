package com.zpi.user.infrastructure;

import com.zpi.user.domain.User;
import lombok.Getter;

import javax.persistence.Id;

@Getter
class UserTuple {
    @Id
    private final String login;
    private final String password;

    public UserTuple(User user) {
        login = user.getLogin();
        password = user.getPassword();
    }

    public static User toDomain(UserTuple tuple) {
        return User.builder()
                .login(tuple.getLogin())
                .password(tuple.getPassword())
                .build();
    }
}
