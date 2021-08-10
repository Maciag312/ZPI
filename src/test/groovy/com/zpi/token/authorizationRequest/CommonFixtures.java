package com.zpi.token.authorizationRequest;

import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.domain.Client;
import com.zpi.common.api.UserDTO;

class CommonFixtures {
    static final String defaultUri = "uri";
    static final String defaultState = "statesdsdr";
    static final String defaultClientId = "id";
    static final String defaultLogin = "Login";
    static final String defaultPassword = "Password";

    static RequestDTO correctRequest() {
        return RequestDTO.builder()
                .clientId(defaultClientId)
                .redirectUri(defaultUri)
                .responseType("code")
                .scope("openid")
                .state(defaultState)
                .build();
    }

    static Client defaultClient() {
        var client = Client.builder()
                .id(defaultClientId)
                .availableRedirectUri(null)
                .build();
        client.addRedirectUri(defaultUri);

        return client;
    }

    static UserDTO defaultUser() {
        return UserDTO.builder()
                .login(defaultLogin)
                .password(defaultPassword)
                .build();
    }
}
