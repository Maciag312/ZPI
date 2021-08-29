package com.zpi.api.authCode.ticketRequest;

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
public class TicketRequestDTO {
    private final String clientId;
    private final String redirectUri;
    private final String responseType;
    private final String scope;
    private final String state;

    public TicketRequestDTO(String clientId, String redirectUri, String responseType, String scope, String state) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.responseType = responseType;
        this.scope = scope;
        this.state = state;
    }

    public AuthenticationRequest toDomain() {
        return AuthenticationRequest.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .responseType(responseType)
                .scope(scopeToList(scope))
                .state(state)
                .build();
    }

    private static List<String> scopeToList(String scope) {
        if (scope == null || scope.equals("")) {
            return null;
        }

        final String delimiter = "%20";
        return List.of(scope.split(delimiter));
    }
}
