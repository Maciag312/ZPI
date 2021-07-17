package com.zpi.token.infrastructure;

import com.zpi.token.domain.WebClient;
import com.zpi.token.domain.WebClientRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryClientRepository implements WebClientRepository {
    private final HashMap<String, WebClientTuple> clients = new HashMap<>();

    @Override
    public void save(String key, WebClient client) {
        clients.put(key, new WebClientTuple(client));
    }

    @Override
    public Optional<WebClient> getByKey(String key) {
        var tuple = Optional.ofNullable(clients.get(key));
        return tuple.map(WebClientTuple::toDomain);
    }
}
