package com.zpi.infrastructure.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.authCode.consentRequest.AuthUserData;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Id;

@Getter
@Data
public class AuthCodeTuple implements EntityTuple<AuthCode> {
    @Id
    private final String id;

    private final String scope;
    private final String redirectUri;
    private final String username;

    AuthCodeTuple(String key, AuthCode data) {
        this.id = key;

        var userData = data.getUserData();
        this.scope = userData.getScope();
        this.redirectUri = userData.getRedirectUri();
        this.username = userData.getUsername();
    }

    @Override
    public AuthCode toDomain() {
        var data = new AuthUserData(scope, redirectUri, username);
        return new AuthCode(id, data);
    }
}