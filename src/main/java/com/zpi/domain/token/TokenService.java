package com.zpi.domain.token;

public interface TokenService {
    Token getToken(TokenRequest request) throws TokenErrorResponseException;

    Token refreshToken(RefreshRequest request) throws TokenErrorResponseException;
}
