package com.zpi.domain.token.tokenRequest.tokenIssuer;

import com.zpi.domain.token.tokenRequest.Token;
import com.zpi.domain.token.tokenRequest.TokenRequest;
import org.springframework.stereotype.Component;

@Component
public class TokenIssuerImpl implements TokenIssuer {
    @Override
    public Token issue(TokenRequest tokenRequest) throws TokenIssuerFailedException {
        return new Token("asdf");
    }
}
