package com.zpi.api.token.ticketRequest;

import com.zpi.domain.token.ticketRequest.request.Request;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RequestDTO {
    private final String clientId;
    private final String redirectUri;
    private final String responseType;
    private final String scope;
    private final String state;

    public Request toDomain() {
        return Request.builder()
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
