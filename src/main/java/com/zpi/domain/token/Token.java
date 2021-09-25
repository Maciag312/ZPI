package com.zpi.domain.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Token {
    private final String accessToken;
    private final String tokenType;
    private final String refreshToken;
    private final String expiresIn;
}
