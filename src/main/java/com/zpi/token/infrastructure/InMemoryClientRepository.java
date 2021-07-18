package com.zpi.token.infrastructure;

import com.zpi.token.domain.Client;
import com.zpi.token.domain.ClientRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryClientRepository implements ClientRepository {
    private final HashMap<String, ClientTuple> clients = new HashMap<>();

    @Override
    public void save(String key, Client client) {
        clients.put(key, new ClientTuple(client));
    }

    @Override
    public Optional<Client> getByKey(String key) {
        var tuple = Optional.ofNullable(clients.get(key));
        return tuple.map(ClientTuple::toDomain);
    }
}
