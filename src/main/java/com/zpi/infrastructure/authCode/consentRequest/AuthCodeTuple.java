package com.zpi.infrastructure.authCode.consentRequest;

import com.zpi.domain.authCode.consentRequest.AuthCode;
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

    AuthCodeTuple(String key, AuthCode data) {
        this.id = key;
        this.scope = data.getScope();
    }

    @Override
    public AuthCode toDomain() {
        return new AuthCode(id, scope);
    }
}