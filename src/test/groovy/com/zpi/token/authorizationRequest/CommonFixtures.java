package com.zpi.token.authorizationRequest;

import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.domain.WebClient;

class CommonFixtures {
    static final String defaultUri = "uri";
    static final String defaultState = "statesdsdr";
    static final String defaultClientId = "id";

    static RequestDTO correctRequest() {
        return RequestDTO.builder()
                .clientId(defaultClientId)
                .redirectUri(defaultUri)
                .responseType("code")
                .scope("openid")
                .state(defaultState)
                .build();
    }

    static WebClient defaultClient() {
        var client = WebClient.builder()
                .id(defaultClientId)
                .availableRedirectUri(null)
                .build();
        client.addRedirectUri(defaultUri);

        return client;
    }
}
