package com.zpi.domain.token;

import com.zpi.domain.token.tokenRequest.Token;
import com.zpi.domain.token.tokenRequest.TokenRequest;

public interface TokenService {
    Token getToken(TokenRequest tokenRequest) throws TokenErrorResponseException;
}
