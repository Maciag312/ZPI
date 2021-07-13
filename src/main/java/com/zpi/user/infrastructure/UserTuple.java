package com.zpi.user.infrastructure;

import com.zpi.user.domain.User;
import com.zpi.utils.IdGenerator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
class UserTuple {
    private final UUID id;
    private final String name;
    private final String surname;
    private final String login;
    private final String password;

    public UserTuple(User user) {
        id = IdGenerator.generateId();
        name = user.getName();
        surname = user.getSurname();
        login = user.getLogin();
        password = user.getPassword();
    }

    public static User toDomain(UserTuple tuple) {
        return User.builder()
                .name(tuple.getName())
                .surname(tuple.getSurname())
                .login(tuple.getLogin())
                .password(tuple.getPassword())
                .build();
    }
}
