package com.zpi.infrastructure.token;

import com.zpi.domain.token.refreshRequest.TokenData;
import com.zpi.infrastructure.common.EntityTuple;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
class TokenTuple implements EntityTuple<TokenData> {
    private final String value;
    private final String scope;
    private final String username;

    TokenTuple(TokenData data) {
        this.value = data.getValue();
        this.scope = data.getScope();
        this.username = data.getUsername();
    }

    @Override
    public TokenData toDomain() {
        return new TokenData(value, scope, username);
    }
}
