package com.zpi.infrastructure.client;

import com.zpi.domain.client.Client;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Getter;

import javax.persistence.Id;
import java.util.HashSet;

@Getter
class ClientTuple implements EntityTuple<Client> {
    @Id
    private final String id;

    private final HashSet<String> availableRedirectUri;

    ClientTuple(Client client) {
        this.id = client.getId();
        this.availableRedirectUri = client.getAvailableRedirectUri();
    }

    @Override
    public Client toDomain() {
        return Client.builder()
                .id(id)
                .availableRedirectUri(availableRedirectUri)
                .build();
    }
}
