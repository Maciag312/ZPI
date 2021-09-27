package com.zpi.domain.token.issuer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenData {
    private final String value;
    private final String scope;
    private final String username;

    public TokenData(String key, UserClaims claims) {
        this.value = key;
        this.scope = claims.getScope();
        this.username = claims.getUsername();
    }
}
