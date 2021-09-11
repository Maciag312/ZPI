package com.zpi.infrastructure.user;

import com.zpi.domain.user.User;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Id;
import java.util.Optional;

@Getter
@Data
class UserTuple implements EntityTuple<User> {

    private final String login;
    private final String password;
    private final String organization;


    UserTuple(User user) {
        login = user.getLogin();
        password = user.getPassword();
        organization = user.getOrganization();
    }

    @Override
    public User toDomain() {
        return User.builder()
                .login(login)
                .password(password)
                .organization(organization)
                .build();
    }
}
