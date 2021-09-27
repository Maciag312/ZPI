package com.zpi.domain.token.validator;

import com.zpi.domain.token.RefreshRequest;
import com.zpi.domain.token.TokenRequest;
import lombok.Getter;

@Getter
class ValidationRequest {
    private final String clientId;
    private final String grantType;
    private final String code;
    private final String scope;

    public ValidationRequest(TokenRequest request) {
        this.clientId = request.getClientId();
        this.grantType = request.getGrantType();
        this.code = request.getCode();
        this.scope = request.getScope();
    }

    public ValidationRequest(RefreshRequest request) {
        this.clientId = request.getClientId();
        this.grantType = request.getGrantType();
        this.code = request.getRefreshToken();
        this.scope = request.getScope();
    }
}
