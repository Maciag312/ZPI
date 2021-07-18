package com.zpi.token.api.authorizationRequest;

import com.zpi.token.domain.authorizationRequest.request.Request;
import lombok.Builder;
import lombok.Getter;

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
                .scope(scope)
                .state(state)
                .build();
    }
}
