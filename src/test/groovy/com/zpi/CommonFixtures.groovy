package com.zpi

import com.zpi.api.client.ClientDTO
import com.zpi.api.common.dto.UserDTO
import com.zpi.domain.client.Client
import com.zpi.api.token.authorizationRequest.RequestDTO
import com.zpi.domain.token.ticketRequest.request.Request

class CommonFixtures {
    public static final String defaultClientId = "id"
    public static final String defaultUri = "uri"
    public static final String defaultResponseType = "code"
    public static final String defaultScope = "openid profile photos asdf_asdf_asdf"
    public static final String defaultState = "statesdsdr"

    public static final String defaultLogin = "Login"
    public static final String defaultPassword = "Password"

    static RequestDTO requestDTO() {
        return RequestDTO.builder()
                .clientId(defaultClientId)
                .redirectUri(defaultUri)
                .responseType(defaultResponseType)
                .scope(defaultScope)
                .state(defaultState)
                .build()
    }

    static Request request() {
        return Request.builder()
                .clientId(defaultClientId)
                .redirectUri(defaultUri)
                .responseType(defaultResponseType)
                .scope(List.of(defaultScope.split(" ")))
                .state(defaultState)
                .build()
    }

    static Client client() {
        var client = Client.builder()
                .id(defaultClientId)
                .availableRedirectUri(null)
                .build()
        client.addRedirectUri(defaultUri)

        return client
    }

    static ClientDTO clientDTO() {
        return ClientDTO.builder()
                .id(defaultClientId)
                .availableRedirectUri(List.of(defaultUri))
                .build()
    }

    static UserDTO userDTO() {
        return UserDTO.builder()
                .login(defaultLogin)
                .password(defaultPassword)
                .build()
    }
}
