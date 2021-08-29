package com.zpi.domain.token.tokenRequest.tokenIssuer;

import com.zpi.domain.common.RequestError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenIssuerFailedException extends Exception {
    private final RequestError<TokenIssuerErrorType> error;
}