package com.zpi.token.infrastructure;

import com.zpi.token.domain.Client;
import lombok.Getter;

import javax.persistence.Id;
import java.util.HashSet;

@Getter
public class ClientTuple {
    @Id
    private final String id;

    private final HashSet<String> availableRedirectUri;

    public static Client toDomain(ClientTuple clientTuple) {
        return Client.builder().id(clientTuple.getId()).availableRedirectUri(clientTuple.getAvailableRedirectUri()).build();
    }

    public ClientTuple(Client client) {
        this.id = client.getId();
        this.availableRedirectUri = client.getAvailableRedirectUri();
    }
}
