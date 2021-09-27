package com.zpi.domain.token.issuer.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class TokenClaims {
    private final Date issuedAt;
    private final Date expirationTime;
}
