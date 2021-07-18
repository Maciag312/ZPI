package com.zpi.token.domain.authorizationRequest.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Request {
    private final String clientId;
    private final String redirectUri;
    private final String responseType;
    private final String scope;
    private final String state;
}
