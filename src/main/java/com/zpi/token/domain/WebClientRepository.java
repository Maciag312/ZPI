package com.zpi.token.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebClientRepository {
    void save(String key, WebClient client);

    Optional<WebClient> getByKey(String id);
}
