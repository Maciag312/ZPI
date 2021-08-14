package com.zpi.client.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository {
    void save(String key, Client client);

    Optional<Client> getByKey(String id);
}
