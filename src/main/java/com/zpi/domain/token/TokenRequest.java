package com.zpi.domain.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRequest {
    private final String grantType;
    private final String code;
    private final String clientId;
    private final String scope;
}
