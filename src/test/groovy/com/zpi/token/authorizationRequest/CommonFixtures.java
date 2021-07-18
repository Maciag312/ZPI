package com.zpi.token.authorizationRequest;

import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.domain.WebClient;
import com.zpi.user.domain.User;
import com.zpi.utils.BasicAuth;

class CommonFixtures {
    static final String defaultUri = "uri";
    static final String defaultState = "statesdsdr";
    static final String defaultClientId = "id";
    static final String defaultLogin = "login";
    static final String defaultPassword = "password";

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

    static BasicAuth defaultAuth() {
        var user = User.builder()
                .login(defaultLogin)
                .password(defaultPassword)
                .build();
        return new BasicAuth(BasicAuth.encodeFrom(user));
    }
}
