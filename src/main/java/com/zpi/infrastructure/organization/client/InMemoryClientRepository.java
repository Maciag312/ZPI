package com.zpi.infrastructure.organization.client;

import com.zpi.domain.organization.client.Client;
import com.zpi.domain.organization.client.ClientRepository;
import com.zpi.infrastructure.common.EntityTuple;
import com.zpi.infrastructure.common.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryClientRepository extends InMemoryRepository<String, Client> implements ClientRepository {
    @Override
    public EntityTuple<Client> fromDomain(Client client) {
        return new ClientTuple(client);
    }
}
