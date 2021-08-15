package com.zpi.infrastructure.user;

import com.zpi.domain.user.User;
import com.zpi.domain.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryUserRepository implements UserRepository {
    private final HashMap<String, UserTuple> users = new HashMap<>();

    @Override
    public void save(String key, User user) {
        users.put(key, new UserTuple(user));
    }

    @Override
    public Optional<User> getByKey(String key) {
        var tuple = Optional.ofNullable(users.get(key));
        return tuple.map(UserTuple::toDomain);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
