package com.zpi.domain.client;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository {
    void save(String key, Client client);

    Optional<Client> getByKey(String id);

    void clear();
}
