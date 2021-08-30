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

    AuthCodeTuple(String key, AuthCode data) {
        this.id = key;
    }

    @Override
    public AuthCode toDomain() {
        return new AuthCode(id);
    }
}