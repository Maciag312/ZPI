package com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Getter
@AllArgsConstructor
public class TokenIssuerConfig {
    private final TokenClaims claims;
    private final long validityInMilliseconds = 3600000L;
    private final String secretKey;
    private final Key key;

    public TokenIssuerConfig(String secretKey) {
        this.secretKey = secretKey;
        this.key = generateKey();
        final String issuer = "asdf";
        final String subject = "asdf";
        final String audience = "asdf";
        final Date issuedAt = new Date();
        final Date expirationTime = new Date(issuedAt.getTime() + validityInMilliseconds);

        claims = new TokenClaims(issuer, subject, audience, issuedAt, expirationTime);
    }

    private SecretKeySpec generateKey() throws IllegalArgumentException, WeakKeyException {
        return new SecretKeySpec(DatatypeConverter.parseBase64Binary(secretKey), SignatureAlgorithm.HS384.getJcaName());
    }
}
