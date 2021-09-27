package com.zpi.domain.token.issuer;

import com.zpi.domain.token.Token;
import com.zpi.domain.token.RefreshRequest;
import com.zpi.domain.token.TokenRequest;

public interface TokenIssuer {
    Token issue(TokenRequest request);
    Token refresh(RefreshRequest request);
}
