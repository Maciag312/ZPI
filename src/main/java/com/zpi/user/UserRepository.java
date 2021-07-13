package com.zpi.user;

import java.util.Optional;

public interface UserRepository {
    void save(String key, User user);

    Optional<User> getByKey(String key);
}
