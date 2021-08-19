package com.zpi.infrastructure.user;

import com.zpi.domain.user.User;
import com.zpi.domain.user.UserRepository;
import com.zpi.infrastructure.common.InMemoryEntityRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryUserRepository extends InMemoryEntityRepository<User, UserTuple> implements UserRepository {
    @Override
    public void save(String key, User user) {
        super.getItems().put(key, new UserTuple(user));
    }
}
