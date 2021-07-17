package com.zpi.token.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebClientRepository {
    Optional<WebClient> getByKey(String id);
}
