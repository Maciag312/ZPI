package com.zpi.domain.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefreshRequest {
    private final String clientId;
    private final String grantType;
    private final String refreshToken;
    private final String scope;
}
