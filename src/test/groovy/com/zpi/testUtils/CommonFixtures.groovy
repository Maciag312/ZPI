package com.zpi.testUtils

import com.zpi.api.authCode.consentRequest.ConsentRequestDTO
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import com.zpi.api.common.dto.UserDTO
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest
import com.zpi.domain.authCode.consentRequest.ConsentRequest
import com.zpi.domain.authCode.consentRequest.TicketData
import com.zpi.domain.rest.ams.Client

class CommonFixtures {
    public static final String clientId = "id"
    public static final String redirectUri = "uri"
    public static final String responseType = "code"
    public static final String scope = "profile photos asdf_asdf_asdf"
    public static final List<String> scopeList = List.of(scope.split(" "))
    public static final String hardcodedScope = "profile"
    public static final List<String> hardcodedScopeList = List.of(hardcodedScope.split(" "))
    public static final String state = "statesdsdr"

    public static final String login = "Login"
    public static final String password = "Password"

    public static final String ticket = "defaultTicketsfasdgfartasdfafta"
    public static final String authPageUrl = "/organization/signin"
    public static final String grantType = "authorization_code"

    static TicketRequestDTO requestDTO() {
        return TicketRequestDTO.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .responseType(responseType)
                .scope(scope)
                .state(state)
                .build()
    }

    static TicketRequestDTO requestOnlyRequiredDTO() {
        return TicketRequestDTO.builder()
                .clientId(clientId)
                .responseType(responseType)
                .state(state)
                .build()
    }

    static AuthenticationRequest request() {
        return AuthenticationRequest.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .responseType(responseType)
                .scope(scopeList)
                .state(state)
                .build()
    }

    static Client client() {
        return new Client(List.of(CommonFixtures.redirectUri), clientId)
    }

    static UserDTO userDTO() {
        return new UserDTO(login, password);
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
        return new TicketData(redirectUri, scope, login)
    }
}
