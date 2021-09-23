package com.zpi.domain.token.tokenRequest.tokenIssuer;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.token.refreshRequest.TokenData;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class UserClaims {
    private final String scope;
    private final String username;
    private final HashMap<String, String> claims;

    private UserClaims(String scope, String username) {
        this.scope = scope;
        this.username = username;

        this.claims = new HashMap<>();
        claims.put("scope", scope);
        claims.put("username", username);
    }

    public UserClaims(AuthCode code) {
        this(code.getUserData().getScope(), code.getUserData().getUsername());
    }

    public UserClaims(TokenData data) {
        this(data.getScope(), data.getUsername());
    }
}
