package com.zpi.domain.authCode.authenticationRequest;

import com.zpi.domain.rest.ams.AmsService;
import com.zpi.domain.rest.ams.Client;
import com.zpi.infrastructure.rest.ams.AmsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public
class OptionalParamsFiller {
    private final AmsService ams;

    private Client client;

    public AuthenticationRequest fill(AuthenticationRequest request) {
        var clientId = request.getClientId();

        this.client = ams.clientDetails(clientId).orElse(null);

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
