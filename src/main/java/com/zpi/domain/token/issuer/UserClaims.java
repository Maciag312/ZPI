package com.zpi.domain.token.issuer;

import com.zpi.domain.authCode.consentRequest.AuthCode;
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
        claims.put("username_hash", username);

        var issuer = "";
        claims.put("iss", issuer);
    }

    public UserClaims(AuthCode code) {
        this(code.getUserData().getScope(), code.getUserData().getUsername());
    }

    public UserClaims(TokenData data) {
        this(data.getScope(), data.getUsername());
    }
}
