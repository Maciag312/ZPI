package com.zpi.domain.token.validator;

import com.zpi.domain.token.TokenRequest;
import com.zpi.domain.token.RefreshRequest;

public interface TokenRequestValidator {
    void validate(TokenRequest tokenRequest) throws ValidationFailedException;
    void validate(RefreshRequest tokenRequest) throws ValidationFailedException;
}
