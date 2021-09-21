package com.zpi.domain.token.tokenRequest.tokenIssuer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenIssuerErrorType {
    UNRECOGNIZED_AUTH_CODE("UNRECOGNIZED_AUTH_CODE");

    private final String name;
}
