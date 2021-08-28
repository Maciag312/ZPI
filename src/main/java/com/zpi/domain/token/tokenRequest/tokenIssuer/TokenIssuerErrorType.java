package com.zpi.domain.token.tokenRequest.tokenIssuer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenIssuerErrorType {
    ERROR("ERROR");

    private final String name;
}
