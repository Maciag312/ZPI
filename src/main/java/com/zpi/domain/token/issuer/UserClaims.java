package com.zpi.domain.token.issuer;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.organization.client.Client;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class UserClaims {
    private final String scope;
    private final String username;
    private final HashMap<String, String> claims;

    private UserClaims(String scope, String username, Client client) {
        this.scope = scope;
        this.username = username;

        this.claims = new HashMap<>();
        claims.put("scope", scope);
        claims.put("username_hash", username);

        var issuer = client == null ? "" : client.getOrganizationName();
        claims.put("iss", issuer);
    }

    public UserClaims(AuthCode code, Client client) {
        this(code.getUserData().getScope(), code.getUserData().getUsername(), client);
    }

    public UserClaims(TokenData data, Client client) {
        this(data.getScope(), data.getUsername(), client);
    }
}
