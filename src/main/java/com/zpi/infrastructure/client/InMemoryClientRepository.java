package com.zpi.infrastructure.client;

import com.zpi.domain.client.Client;
import com.zpi.domain.client.ClientRepository;
import com.zpi.infrastructure.common.InMemoryEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryClientRepository extends InMemoryEntityRepository<Client, ClientTuple> implements ClientRepository {
    @Override
    public void save(String key, Client client) {
        super.getItems().put(key, new ClientTuple(client));
    }
}
