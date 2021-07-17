package com.zpi.user.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    void save(String key, User user);

    Optional<User> getByKey(String key);
}
