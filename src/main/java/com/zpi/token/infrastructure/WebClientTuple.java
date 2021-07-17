package com.zpi.token.infrastructure;

import com.zpi.token.domain.WebClient;
import lombok.Getter;

import javax.persistence.Id;
import java.util.HashSet;

@Getter
public class WebClientTuple {
    @Id
    private final String id;

    private final HashSet<String> availableRedirectUri;

    public static WebClient toDomain(WebClientTuple clientTuple) {
        return WebClient.builder().id(clientTuple.getId()).availableRedirectUri(clientTuple.getAvailableRedirectUri()).build();
    }

    public WebClientTuple(WebClient client) {
        this.id = client.getId();
        this.availableRedirectUri = client.getAvailableRedirectUri();
    }
}
