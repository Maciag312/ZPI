package com.zpi.infrastructure.user;

import com.zpi.domain.user.User;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Getter;

import javax.persistence.Id;

@Getter
class UserTuple implements EntityTuple<User> {
    @Id
    private final String login;
    private final String password;

    UserTuple(User user) {
        login = user.getLogin();
        password = user.getPassword();
    }

    @Override
    public User toDomain() {
        return User.builder()
                .login(login)
                .password(password)
                .build();
    }
}
