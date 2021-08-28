package com.zpi.domain.token;

import com.zpi.domain.token.tokenRequest.TokenErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenErrorResponseException extends Exception {
    private final TokenErrorResponse response;
}
