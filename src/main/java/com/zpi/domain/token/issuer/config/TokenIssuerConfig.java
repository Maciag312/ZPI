package com.zpi.domain.token.issuer.config;

import com.zpi.domain.rest.ams.AuthConfiguration;
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
    private final String secretKey;
    private final long validityInMilliseconds;
    private final Key key;

    public TokenIssuerConfig(AuthConfiguration config) {
        this.secretKey = config.getSecretKey();
        this.validityInMilliseconds = config.getExpirationTime();
        this.key = generateKey();

        final Date issuedAt = new Date();
        final Date expirationTime = new Date(issuedAt.getTime() + config.getExpirationTime());

        claims = new TokenClaims(issuedAt, expirationTime);
    }

    private SecretKeySpec generateKey() throws IllegalArgumentException, WeakKeyException {
        return new SecretKeySpec(DatatypeConverter.parseBase64Binary(secretKey), SignatureAlgorithm.HS384.getJcaName());
    }
}
