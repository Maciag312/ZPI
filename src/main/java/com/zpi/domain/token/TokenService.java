package com.zpi.domain.token;

import com.zpi.domain.token.refreshRequest.RefreshRequest;
import com.zpi.domain.token.tokenRequest.Token;
import com.zpi.domain.token.tokenRequest.TokenRequest;

public interface TokenService {
    Token getToken(TokenRequest request) throws TokenErrorResponseException;

    Token refreshToken(RefreshRequest request) throws TokenErrorResponseException;
}
