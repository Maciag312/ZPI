package com.zpi;

import com.zpi.client.api.ClientDTO;
import com.zpi.client.domain.Client;
import com.zpi.common.api.dto.UserDTO;
import com.zpi.token.api.authorizationRequest.RequestDTO;
import com.zpi.token.domain.authorizationRequest.request.Request;

import java.util.List;

public class CommonFixtures {
    public static final String defaultClientId = "id";
    public static final String defaultUri = "uri";
    public static final String defaultResponseType = "code";
    public static final String defaultScope = "openid profile photos asdf_asdf_asdf";
    public static final String defaultState = "statesdsdr";

    public static final String defaultLogin = "Login";
    public static final String defaultPassword = "Password";

    public static RequestDTO requestDTO() {
        return RequestDTO.builder()
                .clientId(defaultClientId)
                .redirectUri(defaultUri)
                .responseType(defaultResponseType)
                .scope(defaultScope)
                .state(defaultState)
                .build();
    }

    public static Request request() {
        return Request.builder()
                .clientId(defaultClientId)
                .redirectUri(defaultUri)
                .responseType(defaultResponseType)
                .scope(List.of(defaultScope.split(" ")))
                .state(defaultState)
                .build();
    }

    public static Client client() {
        var client = Client.builder()
                .id(defaultClientId)
                .availableRedirectUri(null)
                .build();
        client.addRedirectUri(defaultUri);

        return client;
    }

    public static ClientDTO clientDTO() {
        return ClientDTO.builder()
                .id(defaultClientId)
                .availableRedirectUri(List.of(defaultUri))
                .build();
    }

    public static UserDTO userDTO() {
        return UserDTO.builder()
                .login(defaultLogin)
                .password(defaultPassword)
                .build();
    }
}
