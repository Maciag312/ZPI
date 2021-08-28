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

    public AuthenticationRequest fill(AuthenticationRequest request) {
        var clientId = request.getClientId();

        this.client = repository.getByKey(clientId).orElse(null);

        var redirectUri = fillRedirectUri(request);
        var responseType = request.getResponseType();
        var scope = fillScope(request);
        var state = request.getState();

        return AuthenticationRequest.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .responseType(responseType)
                .scope(scope)
                .state(state)
                .build();
    }

    private String fillRedirectUri(AuthenticationRequest request) {
        if (request.getRedirectUri() == null) {
            var availableRedirectUri = client.getAvailableRedirectUri();

            return availableRedirectUri.stream().findFirst().orElse(null);
        }

        return request.getRedirectUri();
    }

    private List<String> fillScope(AuthenticationRequest request) {
        if (request.getScope() == null) {
            return client.getHardcodedDefaultScope();
        }

        return request.getScope();
    }
}
