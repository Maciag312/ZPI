package com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class TokenClaims {
    private final String issuer;
    private final String subject;
    private final String audience;
    private final Date issuedAt;
    private final Date expirationTime;
}
