package com.zpi.user.domain;

import java.util.Optional;

public interface UserRepository {
    void save(String key, User user);

    Optional<User> getByKey(String key);
}
