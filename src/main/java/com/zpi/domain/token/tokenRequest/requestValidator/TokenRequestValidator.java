package com.zpi.domain.token.tokenRequest.requestValidator;

import com.zpi.domain.token.tokenRequest.TokenRequest;

public interface TokenRequestValidator {
    void validate(TokenRequest tokenRequest) throws ValidationFailedException;
}
