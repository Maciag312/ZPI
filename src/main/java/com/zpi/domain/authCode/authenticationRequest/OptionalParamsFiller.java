package com.zpi.domain.authCode.authenticationRequest;

import com.zpi.domain.client.Client;
import com.zpi.domain.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public
class OptionalParamsFiller {
    private final ClientRepository repository;

    private Client client;

    public Request fill(Request request) {
        var clientId = request.getClientId();

        this.client = repository.getByKey(clientId).orElse(null);

        var redirectUri = fillRedirectUri(request);
        var responseType = request.getResponseType();
        var scope = fillScope(request);
        var state = request.getState();

        return Request.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .responseType(responseType)
                .scope(scope)
                .state(state)
                .build();
    }

    private String fillRedirectUri(Request request) {
        if (request.getRedirectUri() == null) {
            var availableRedirectUri = client.getAvailableRedirectUri();

            return availableRedirectUri.stream().findFirst().orElse(null);
        }

        return request.getRedirectUri();
    }

    private List<String> fillScope(Request request) {
        if (request.getScope() == null) {
            return client.getHardcodedDefaultScope();
        }

        return request.getScope();
    }
}
