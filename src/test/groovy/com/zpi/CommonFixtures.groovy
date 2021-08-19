package com.zpi

import com.zpi.api.authCode.consentRequest.ConsentRequestDTO
import com.zpi.api.authCode.ticketRequest.RequestDTO
import com.zpi.api.client.ClientDTO
import com.zpi.api.common.dto.UserDTO
import com.zpi.domain.authCode.authenticationRequest.Request
import com.zpi.domain.authCode.consentRequest.ConsentRequest
import com.zpi.domain.authCode.consentRequest.TicketData
import com.zpi.domain.client.Client

class CommonFixtures {
    public static final String clientId = "id"
    public static final String redirectUri = "uri"
    public static final String responseType = "code"
    public static final String scope = "openid profile photos asdf_asdf_asdf"
    public static final String state = "statesdsdr"

    public static final String login = "Login"
    public static final String password = "Password"

    public static final String ticket = "defaultTicketsfasdgfartasdfafta"
    public static final String authPageUrl = "/signin"

    static RequestDTO requestDTO() {
        return RequestDTO.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .responseType(responseType)
                .scope(scope)
                .state(state)
                .build()
    }

    static Request request() {
        return Request.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .responseType(responseType)
                .scope(List.of(scope.split(" ")))
                .state(state)
                .build()
    }

    static Client client() {
        var client = Client.builder()
                .id(clientId)
                .availableRedirectUri(null)
                .build()
        client.addRedirectUri(redirectUri)

        return client
    }

    static ClientDTO clientDTO() {
        return ClientDTO.builder()
                .id(clientId)
                .availableRedirectUri(List.of(redirectUri))
                .build()
    }

    static UserDTO userDTO() {
        return UserDTO.builder()
                .login(login)
                .password(password)
                .build()
    }

    static ConsentRequest consentRequest() {
        return ConsentRequest.builder()
                .ticket(ticket)
                .state(state)
                .build()
    }

    static ConsentRequestDTO consentRequestDTO(String ticket) {
        return ConsentRequestDTO.builder()
                .ticket(ticket)
                .state(state)
                .build()
    }

    static TicketData ticketData() {
        return TicketData.builder()
                .redirectUri(redirectUri)
                .build();
    }
}
