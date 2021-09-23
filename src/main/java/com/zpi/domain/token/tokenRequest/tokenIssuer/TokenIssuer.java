package com.zpi.domain.token.tokenRequest.tokenIssuer;

import com.zpi.domain.token.refreshRequest.RefreshRequest;
import com.zpi.domain.token.tokenRequest.Token;
import com.zpi.domain.token.tokenRequest.TokenRequest;

public interface TokenIssuer {
    Token issue(TokenRequest request) throws TokenIssuerFailedException;
    Token refresh(RefreshRequest request) throws TokenIssuerFailedException;
}
