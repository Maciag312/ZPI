package com.zpi.domain.token.tokenRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRequest {
    private final String grantType;
    private final String code;
    private final String redirectUri;
    private final String clientId;
}
