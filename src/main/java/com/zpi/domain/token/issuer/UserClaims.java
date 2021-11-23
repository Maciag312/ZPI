package com.zpi.domain.token.issuer;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.rest.ams.UserInfo;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class UserClaims {
    private final String scope;
    private final String email;
    private final HashMap<String, String> claims;

    private UserClaims(String scope, UserInfo userInfo) {
        this.scope = scope;
        this.email = userInfo.getEmail();

        this.claims = new HashMap<>();
        claims.put("scope", scope);
        claims.put("username", email);
        claims.put("permissions", userInfo.getPermissions());
        claims.put("roles", userInfo.getRoles());

        var issuer = "AUTH_SERVER";
        claims.put("iss", issuer);
    }

    public UserClaims(AuthCode code, UserInfo userInfo) {
        this(code.getUserData().getScope(), userInfo);
    }

    public UserClaims(TokenData data, UserInfo userInfo) {
        this(data.getScope(), userInfo);
    }
}
